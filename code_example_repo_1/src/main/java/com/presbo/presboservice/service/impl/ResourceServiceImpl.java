package com.presbo.presboservice.service.impl;

import com.presbo.presboservice.dto.req.PersistResourceReqDto;
import com.presbo.presboservice.dto.req.PersistTaskReqDto;
import com.presbo.presboservice.dto.res.AssignedResourceDto;
import com.presbo.presboservice.dto.res.ResourceResDto;
import com.presbo.presboservice.entity.*;
import com.presbo.presboservice.repository.AssignedResourceRepository;
import com.presbo.presboservice.repository.ResourceRepository;
import com.presbo.presboservice.service.ProjectService;
import com.presbo.presboservice.service.ResourceService;
import com.presbo.presboservice.service.TaskService;
import com.presbo.presboservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ResourceServiceImpl implements ResourceService{

    @Autowired
    ResourceRepository resourceRepo;

    @Autowired
    ProjectService projectService;

    @Autowired
    TaskService taskService;

    @Autowired
    UserService userService;

    @Autowired
    AssignedResourceRepository assignResourceRepo;

    @Override
    public void saveResource(Resource resource) {
        resourceRepo.save(resource);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED)
    public void persistResource(PersistResourceReqDto data) {

        Project project = projectService.findProjectById(data.getProjectId());
        Task task;

        Resource resource = this.findResourceByUniqueIdAndProjectId(data.getUniqueId(), data.getProjectId()).orElse(null);

        if(resource == null){
            resource = new Resource();
            resource.setName(data.getName());
            resource.setProject(project);
            resource.setTasks(new HashSet<>());
            resource.setUniqueId(Long.valueOf(""+data.getUniqueId()));
            this.saveResource(resource);
        }

        Set<Resource> resourceList;

        for(PersistTaskReqDto persistTaskReqDto : data.getTasks()) {
            task = resource.getTasks().stream().filter(o -> o.getUniqueId().equals(persistTaskReqDto.getUniqueId())).findFirst().orElse(null);

            if (task == null) {
                task = new Task();

                task.setWbs(persistTaskReqDto.getWbs());
                task.setRemainingDuration("" + persistTaskReqDto.getRemainingDuration());
                task.setPercentageComplete(persistTaskReqDto.getPercentageComplete() + "%");
                task.setParentTaskId(persistTaskReqDto.getParentTaskId());
                task.setParentTaskName(persistTaskReqDto.getParentTaskName());
                task.setParentTaskWbs(persistTaskReqDto.getParentTaskWbs());
                task.setDurationComplete("" + persistTaskReqDto.getDurationComplete());
                task.setName(persistTaskReqDto.getName());
                task.setUniqueId(Long.valueOf("" + persistTaskReqDto.getUniqueId()));
                task.setTaskId(persistTaskReqDto.getTaskId());
                task.setResources(new HashSet<>());
                task.setStartDate(persistTaskReqDto.getStartDate());
                task.setFinishDate(persistTaskReqDto.getFinishDate());
                task.setIsStarted(persistTaskReqDto.isStarted());
                task.setIsUpdated(persistTaskReqDto.isUpdated());
                task.setNotes(persistTaskReqDto.getNotes());

            }
            resourceList = task.getResources();
            resourceList.add(resource);
            task.setResources(resourceList);
            taskService.saveTask(task);
        }
    }

    @Override
    public Optional<Resource> findResourceByUniqueIdAndProjectId(Long resourceId, Long projectId) {
        return resourceRepo.findByUniqueIdAndProjectId(resourceId, projectId);
    }

    @Override
    public Set<Resource> findAllProjectResources(Long projectId) {
        return null;
    }

    @Override
    public boolean deleteResourcesByProject(List<Resource> resources) {


        resourceRepo.deleteAll(resources);
       return true;
    }

    @Override
    public void deleteListOfResourcesByUniqueIdAndProjectId(List<Long> resourceUniqueIds, Long projectId) {

        List<Resource> resourcesToBeDeleted = new ArrayList<>();
        Resource resource;
        for(Long resourceUniqueId : resourceUniqueIds){
            resource = this.findResourceByUniqueIdAndProjectId(resourceUniqueId, projectId).get();
            resourcesToBeDeleted.add(resource);
        }

        this.deleteResourcesByProject(resourcesToBeDeleted);

    }

    @Override
    public List<ResourceResDto> getAllResourcesByProject(Long projectId) {

        Project project = projectService.findProjectById(projectId);
        List<ResourceResDto> resources = new ArrayList<>();
        ResourceResDto resourceResDto;

        for(Resource resource : project.getResources()){
            resourceResDto = new ResourceResDto();

            if(resource.getName() == null){
                resource.setName("No assigned Resource Name. Please update project file");
            }
            if(!resource.getTasks().isEmpty()){
                resourceResDto.setId("" + resource.getId());
                resourceResDto.setName(resource.getName());
                resourceResDto.setUniqueId(resource.getUniqueId());
                resources.add(resourceResDto);
            }
        }

        return resources;
    }


    @Override
    public Optional<Resource> findResourceById(Long resourceId) {
        return resourceRepo.findById(resourceId);
    }

    @Override
    public List<AssignedResourceDto> findAssignedResources(String username) {

        User user = userService.findUserByUsername(username);
        Resource resource;
        List<AssignedResourceDto> resources = new ArrayList<>();
        AssignedResourceDto assignedResourceResDto;

        for(AssignedResource assignedResource : user.getAssignedResources()){
            resource = findResourceById(assignedResource.getResourceId()).get();
            assignedResourceResDto = new AssignedResourceDto();
            assignedResourceResDto.setId(resource.getId());
            assignedResourceResDto.setName(resource.getName());
            assignedResourceResDto.setProjectName(resource.getProject().getName());
            resources.add(assignedResourceResDto);
        }

        return resources;

    }

    @Override
    public void removeAllAssignedResourcesByProjectId(Long projectId) {
        Project project = projectService.findProjectById(projectId);

        for(Resource resource : project.getResources()){
            AssignedResource ar = assignResourceRepo.findByResourceId(resource.getId()).orElse(null);
            if(ar != null){
                assignResourceRepo.delete(ar);
            }
        }
    }


}