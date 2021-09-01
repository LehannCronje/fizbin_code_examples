package com.presbo.presboservice.service.impl;

import com.presbo.presboservice.dto.req.TaskListFilterReqDto;
import com.presbo.presboservice.dto.req.TxnUpdateTaskLogReqDto;
import com.presbo.presboservice.dto.req.UploadDocumentReqDto;
import com.presbo.presboservice.dto.res.ProjectResDto;
import com.presbo.presboservice.dto.res.ResourceResDto;
import com.presbo.presboservice.dto.res.TaskResDto;
import com.presbo.presboservice.entity.*;
import com.presbo.presboservice.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Service
public class MobileServiceImpl implements MobileService {

    @Autowired
    UserService userService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    TaskService taskService;

    @Autowired
    TxnUpdateTaskLogService txnUpdateTaskLogService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ImageService imageService;

    @Autowired
    ProjectFileService projectFileService;

    @Override
    public List<ProjectResDto> getAllMobileUserProjects(String username) {

        User user = userService.findUserByUsername(username);
        Resource resource = new Resource();
        Project project = new Project();
        ProjectResDto projectResDto = new ProjectResDto();
        List<ProjectResDto> projects = new ArrayList<>();


        for(AssignedResource assignedResource : user.getAssignedResources()){

            projectResDto = new ProjectResDto();
            resource = resourceService.findResourceById(assignedResource.getResourceId()).get();
            project = resource.getProject();

            projectResDto.setId("" + project.getId());
            projectResDto.setName(project.getName());
            if(!projects.contains(projectResDto)){
                projects.add(projectResDto);
            }


        }


        return projects;
    }

    @Override
    public List<ResourceResDto> getAllMobileResources(String username, Long projectId) {

        User user = userService.findUserByUsername(username);
        Resource resource;
        ResourceResDto resourceResDto;
        List<ResourceResDto> resources = new ArrayList<>();

        for(AssignedResource assignedResource: user.getAssignedResources()){

            resourceResDto = new ResourceResDto();
            resource = resourceService.findResourceById(assignedResource.getResourceId()).get();

            if(resource.getProject().getId().equals(projectId)){
                resourceResDto.setName(resource.getName());
                resourceResDto.setId(""+resource.getId());

                resources.add(resourceResDto);
            }

        }

        return resources;
    }

    @Override
    public List<TaskResDto> getAllMobileTasksByResourceId(Long resourceId) throws ParseException {

        //double check on filter
        TaskListFilterReqDto taskListFilterReqDto = new TaskListFilterReqDto();
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, 14); //2 weeks
        taskListFilterReqDto.setStartDate(currentDate);
        taskListFilterReqDto.setEndDate(calendar.getTime());

        return taskService.getAllTasksByResourceId(resourceId, taskListFilterReqDto);

    }

    @Override
    public void updateTask(TxnUpdateTaskLogReqDto txnUpdateTaskLogReqDto) {

        TxnUpdateTaskLog txnUpdateTaskLog = new TxnUpdateTaskLog();
        PcdUpdatedTask pcdUpdatedTask = new PcdUpdatedTask();

        Project project = projectService.findProjectById(txnUpdateTaskLogReqDto.getProjectId());
        Task task = taskService.findTaskById(txnUpdateTaskLogReqDto.getTaskId()).get();
        task.setIsUpdated(true);

        taskService.saveTask(task);

        BeanUtils.copyProperties(txnUpdateTaskLogReqDto, pcdUpdatedTask);

        pcdUpdatedTask.setTxnUpdateTaskLog(txnUpdateTaskLog);

        txnUpdateTaskLog.setProject(project);
        txnUpdateTaskLog.setTask(task);
        txnUpdateTaskLog.setPcdUpdatedTask(pcdUpdatedTask);

        txnUpdateTaskLogService.insertTxnUpdateTaskLog(txnUpdateTaskLog);

    }

    @Override
    public void handleImages(List<String> files, Long taskId, Long projectId) {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UploadDocumentReqDto uploadDocumentReqDto;
        Image image;
        Project project = projectService.findProjectById(projectId);
        for(String fileBase64 : files){
            uploadDocumentReqDto = new UploadDocumentReqDto();
            Date currentDate = new Date();
            String filename = "Task"+"-"+taskId+"-"+ currentDate.getTime()+ ".jpg";
            byte[] fileByteArray = Base64.getDecoder().decode(fileBase64);
            image = new Image();
            image.setGallery(project.getGallery());
            project.getGallery().setUpdatedAt(currentDate);
            image.setName(filename);
            image.setUrl("image/"+image.getName());
            image.setCreatedAt(currentDate);
            image.setCreatedBy(userDetails.getUsername());
            uploadDocumentReqDto.setDocumentName(filename);
            uploadDocumentReqDto.setDocumentByteArray(fileByteArray);
//            TODO add image size
            Long fileId = projectFileService.uploadDocument(uploadDocumentReqDto, projectId);
            image.setProjectFileId(fileId);
            imageService.saveImage(image);
        }

    }
}
