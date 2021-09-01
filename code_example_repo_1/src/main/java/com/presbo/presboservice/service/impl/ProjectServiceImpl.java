package com.presbo.presboservice.service.impl;

import com.presbo.presboservice.dto.req.*;
import com.presbo.presboservice.dto.res.GetDocumentResDto;
import com.presbo.presboservice.dto.res.ProjectResDto;
import com.presbo.presboservice.dto.res.ResourceResDto;
import com.presbo.presboservice.entity.*;
import com.presbo.presboservice.messaging.sender.GetDocumentMsgSender;
import com.presbo.presboservice.messaging.sender.PersistResourceMsgSender;
import com.presbo.presboservice.repository.ProjectRepository;
import com.presbo.presboservice.service.*;
import com.presbo.presboservice.utility.MpxjUtility;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    ProjectRepository projectRepo;

    @Autowired
    OrganisationService organisationService;

    @Autowired
    TaskService taskService;

    @Autowired
    ProjectFileService projectFileService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    PersistResourceMsgSender persistResourceMsgSender;

    @Autowired
    GetDocumentMsgSender getDocumentMsgSender;

    @Autowired
    GalleryService galleryService;

    @Autowired
    UserService userService;

    @Override
    public Project createProject(ProjectReqDto projectReqDto) {

        Project project = new Project();
        Gallery gallery = new Gallery();
        Organisation organisation = organisationService.findOrgById(projectReqDto.getOrganisationId());
        Date currentDate = new Date();
        project.setName(projectReqDto.getProjectName());
        project.setIsLocked(false);
        project.setOrganisation(organisation);
        this.saveProject(project);

        gallery.setName(project.getName() + " gallery");
        gallery.setImageList(new ArrayList<>());
        gallery.setProject(project);
        gallery.setUpdatedAt(currentDate);
        galleryService.saveGallery(gallery);

        return project;
    }

    //should this be private
    @Override
    public List<PersistResourceReqDto> extractProject(ProjectReqDto projectReqDto) {

        net.sf.mpxj.ProjectFile projectFile = null;
        Project project = this.findProjectById(projectReqDto.getProjectId());

        try {
            project.setCurrentFileId(this.uploadFileWithProjectReqDto(projectReqDto));
            projectFile = MpxjUtility.readProjectFile(projectReqDto.getProjectFile());
        } catch (MPXJException | IOException e) {
            e.printStackTrace();
        }

        List<PersistResourceReqDto> persistData = new ArrayList<>();
        PersistResourceReqDto data;

        assert projectFile != null;


        project.setStatusDate(projectFile.getProjectProperties().getStatusDate());

        this.saveProject(project);

        for (net.sf.mpxj.Resource projectFileResource : projectFile.getResources()) {
            if(projectFileResource.getUniqueID().equals(0)){
                projectFileResource.setName("No Assigned Resource");
                for(net.sf.mpxj.Task projectFileTask: projectFile.getTasks()){
                    if(projectFileTask.getUniqueID() != null || !projectFileTask.getWBS().equals("0")){
                        if(projectFileTask.getResourceAssignments().isEmpty()){
                            projectFileResource.getTaskAssignments().add(new ResourceAssignment(projectFile, projectFileTask));
                        }
                    }
                }
            }
            if(projectFileResource.getType().equals(ResourceType.WORK)){
                data = this.prepareResourceAndTaskPersistData(projectFileResource, projectReqDto.getProjectId());
                persistData.add(data);
            }
        }

        return persistData;

    }

    @Override
    public void persistExtractedProjectData(List<PersistResourceReqDto> persistData) {

        for(PersistResourceReqDto data : persistData){
            try {
                persistResourceMsgSender.persistResourceMsgSender("direct-exchange", "persist-resource", data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void updateProject(ProjectReqDto projectReqDto) {

        Project project = this.findProjectById(projectReqDto.getProjectId());
        List<PersistResourceReqDto> extractedProjectList = this.extractProject(projectReqDto);

        for(Resource projectResource : project.getResources()){
            taskService.deleteTasksByResource(new ArrayList<>(projectResource.getTasks()));
        }

        this.persistExtractedProjectData(extractedProjectList);

        this.deleteResourcesRemovedFromFile(projectReqDto.getProjectId() ,extractedProjectList.stream().map(PersistResourceReqDto::getUniqueId).collect(Collectors.toList()));

    }

    @Override
    public void deleteProject(Long projectId) {
        resourceService.removeAllAssignedResourcesByProjectId(projectId);
        projectRepo.deleteById(projectId);

    }

    @Override
    public void saveProject(Project project) {

        try {
            projectRepo.save(project);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Project findProjectById(Long projectId) {

        return projectRepo.findById(projectId).orElse(null);

    }

    @Override
    public List<ProjectResDto> getAllProjects(Long organisationId) throws Exception {

        Organisation organisation = organisationService.findOrgById(organisationId);

        List<ProjectResDto> projects = new ArrayList<ProjectResDto>();
        ProjectResDto projectResDto = new ProjectResDto();

        GetDocumentReqDto getDocumentReqDto;
        GetDocumentResDto getDocumentResDto;
        GetDocumentResDto latestDocumentResDto = new GetDocumentResDto();

        List<Project> organisationProjects = organisation.getProjects();

        if(organisationProjects.isEmpty()){
            return new ArrayList<>();
        }else{
            for(Project project : organisation.getProjects()){

                projectResDto = new ProjectResDto();
                latestDocumentResDto = new GetDocumentResDto();

                projectResDto.setName(project.getName());
                projectResDto.setId("" + project.getId());
                projectResDto.setIsLocked(project.getIsLocked());
                projectResDto.setStatusDate(project.getStatusDate());

                if(project.getCurrentFileId() != null){
                    getDocumentReqDto = new GetDocumentReqDto();
                    latestDocumentResDto = new GetDocumentResDto();
                    getDocumentReqDto.setDocumentId(project.getCurrentFileId());
                    getDocumentReqDto.setFieldNames(Arrays.asList("name","uploadDate"));
                    getDocumentResDto = getDocumentMsgSender.getDocumentMessage("direct-exchange", "get-document", getDocumentReqDto);
                    if(latestDocumentResDto.getUploadDate() == null){
                        BeanUtils.copyProperties(getDocumentResDto, latestDocumentResDto);
                    }

                    if(getDocumentResDto.getUploadDate().after(latestDocumentResDto.getUploadDate())) {
                        BeanUtils.copyProperties(getDocumentResDto, latestDocumentResDto);
                    }
                }




                if(latestDocumentResDto.getName() == null){
                    projectResDto.setFileName("No project file yet");
                }else{
                    projectResDto.setFileName(latestDocumentResDto.getName());
                }
                projectResDto.setUploadDate(latestDocumentResDto.getUploadDate());
                projects.add(projectResDto);
            }
            return projects;
        }

    }

    @Override
    public void lockProject(Long projectId) {

        Project project = this.findProjectById(projectId);
        project.setIsLocked(true);
        projectRepo.save(project);

    }

    @Override
    public void unlockProject(Long projectId) {

        Project project = this.findProjectById(projectId);
        project.setIsLocked(false);
        projectRepo.save(project);

    }

    private Long uploadFileWithProjectReqDto(ProjectReqDto projectReqDto) throws IOException {

        UploadDocumentReqDto uploadDocumentReqDto = new UploadDocumentReqDto();
        uploadDocumentReqDto.setDocumentName(projectReqDto.getProjectFile().getOriginalFilename());
        uploadDocumentReqDto.setDocumentByteArray(projectReqDto.getProjectFile().getBytes());
        uploadDocumentReqDto.setSize(projectReqDto.getProjectFile().getSize());

        return projectFileService.uploadDocument(uploadDocumentReqDto, projectReqDto.getProjectId());

    }

    private PersistResourceReqDto prepareResourceAndTaskPersistData(net.sf.mpxj.Resource projectFileResource, Long projectId) {

        PersistTaskReqDto taskData;

        PersistResourceReqDto data = new PersistResourceReqDto();
        data.setName(projectFileResource.getName());
        data.setUniqueId(Long.valueOf("" + projectFileResource.getUniqueID()));
        data.setProjectId(projectId);
        data.setTasks(new ArrayList<>());

        for (ResourceAssignment resourceAssignment : projectFileResource.getTaskAssignments()) {
            taskData = new PersistTaskReqDto();

            net.sf.mpxj.Task resourceAssignmentTask = resourceAssignment.getTask();

            if(resourceAssignmentTask.getActive()){
                taskData.setWbs(resourceAssignmentTask.getWBS());
                taskData.setRemainingDuration("" + resourceAssignmentTask.getRemainingDuration());
                taskData.setPercentageComplete(""+resourceAssignmentTask.getPercentageComplete());
                if(resourceAssignmentTask.getParentTask() != null){
                    taskData.setParentTaskId(Long.valueOf(resourceAssignmentTask.getParentTask().getID()));
                    taskData.setParentTaskName(resourceAssignmentTask.getParentTask().getName());
                    taskData.setParentTaskWbs(resourceAssignmentTask.getParentTask().getWBS());
                }
                taskData.setDurationComplete("" + resourceAssignmentTask.getDuration());
                taskData.setName(resourceAssignmentTask.getName());
                taskData.setUniqueId(Long.valueOf("" + resourceAssignmentTask.getUniqueID()));
                taskData.setTaskId(Long.valueOf("" +resourceAssignmentTask.getID()));
                taskData.setStartDate(resourceAssignmentTask.getStart());
                taskData.setFinishDate(resourceAssignment.getFinish());
                taskData.setStarted(resourceAssignmentTask.getActualStart() != null);
                taskData.setUpdated(false);
                taskData.setNotes(resourceAssignment.getNotes());

                data.getTasks().add(taskData);
            }

        }

        return data;
    }

    private void deleteResourcesRemovedFromFile(Long projectId, List<Long> preparedResourceUniqueIds){

        List<Long> databaseResources = resourceService.getAllResourcesByProject(projectId).stream().map(ResourceResDto::getUniqueId).collect(Collectors.toList());
        databaseResources.removeAll(preparedResourceUniqueIds);

        resourceService.deleteListOfResourcesByUniqueIdAndProjectId(databaseResources, projectId);


    }


}