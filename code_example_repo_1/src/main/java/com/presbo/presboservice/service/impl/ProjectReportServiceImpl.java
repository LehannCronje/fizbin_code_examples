package com.presbo.presboservice.service.impl;

import com.presbo.presboservice.dto.req.GenerateReportReqDto;
import com.presbo.presboservice.dto.req.GetReportReqDto;
import com.presbo.presboservice.dto.req.TaskListFilterReqDto;
import com.presbo.presboservice.dto.req.TaskUpdateReportReqDto;
import com.presbo.presboservice.dto.res.GenerateReportResDto;
import com.presbo.presboservice.dto.res.GetReportResDto;
import com.presbo.presboservice.entity.Project;
import com.presbo.presboservice.entity.ProjectReport;
import com.presbo.presboservice.entity.Resource;
import com.presbo.presboservice.entity.TxnUpdateTaskLog;
import com.presbo.presboservice.messaging.sender.GenerateReportMsgSender;
import com.presbo.presboservice.messaging.sender.GetReportMsgSender;
import com.presbo.presboservice.repository.ProjectReportRepository;
import com.presbo.presboservice.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProjectReportServiceImpl implements ProjectReportService {

    @Autowired
    GenerateReportMsgSender generateReportMsgSender;

    @Autowired
    GetReportMsgSender getReportMsgSender;

    @Autowired
    ProjectReportRepository projectReportRepo;

    @Autowired
    ProjectService projectService;

    @Autowired
    TaskService taskService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    TxnUpdateTaskLogService txnUpdateTaskLogService;

    @Override
    public List<GenerateReportResDto> generateReport(GenerateReportReqDto generateReportReqDto) {

        String reportType = generateReportReqDto.getType();

        switch (reportType){
            case "taskList" : return this.taskListReportHandler(generateReportReqDto);
            case "updateTask" : return this.taskUpdateReportHandler(generateReportReqDto);
            default: return new ArrayList<>();
        }
    }

    @Override
    public GetReportResDto getReport(GetReportReqDto getReportReqDto) {

        try {
            return getReportMsgSender.getReportMessage("direct-exchange", "get-report", getReportReqDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //Reiterate. Confusing. Not needed
    private GenerateReportResDto generateTaskListReport(Long resourceId, TaskListFilterReqDto taskListFilterReqDto) throws Exception {

        GenerateReportReqDto generateReportReqDto = new GenerateReportReqDto();

        Resource resource = resourceService.findResourceById(resourceId).get();

        generateReportReqDto.setType("taskList");
        generateReportReqDto.setTaskList(taskService.getAllTasksByResourceId(resourceId, taskListFilterReqDto));
        generateReportReqDto.setProjectName(resource.getProject().getName());
        generateReportReqDto.setResourceName(resource.getName());
        generateReportReqDto.setProjectStatusDate(resource.getProject().getStatusDate());

        return generateReportMsgSender.sendGenerateReportMessage("direct-exchange", "generate-report", generateReportReqDto);
    }

    //Reiterate. Confusing.
    private List<GenerateReportResDto> taskListReportHandler(GenerateReportReqDto generateReportReqDto) {

        GenerateReportResDto generateReportResDto;
        ProjectReport projectReport;

        List<GenerateReportResDto> generatedReportList = new ArrayList<>();


        try {

            for (Long resourceId : generateReportReqDto.getResourceIds()) {
                generateReportResDto = this.generateTaskListReport(resourceId, generateReportReqDto.getFilterData());

                projectReport = new ProjectReport();
                Project project = projectService.findProjectById(generateReportReqDto.getProjectId());
                projectReport.setProject(project);
                projectReport.setReportId(generateReportResDto.getReportId());

                projectReportRepo.save(projectReport);
                generatedReportList.add(generateReportResDto);

            }
            return generatedReportList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private List<GenerateReportResDto> taskUpdateReportHandler(GenerateReportReqDto generateReportReqDto){

        Project project = projectService.findProjectById(generateReportReqDto.getProjectId());
        generateReportReqDto.setProjectName(project.getName());

        Map<Long, TaskUpdateReportReqDto> updateTasks = new HashMap<>();
        TaskUpdateReportReqDto taskUpdateReportReqDto;
        Long taskUniqueId;
        for(TxnUpdateTaskLog txnUpdateTaskLog : txnUpdateTaskLogService.getAllTaskUpdateLogs(generateReportReqDto.getProjectId())){
            taskUniqueId = txnUpdateTaskLog.getTask().getUniqueId();
            taskUpdateReportReqDto = new TaskUpdateReportReqDto();
            BeanUtils.copyProperties(txnUpdateTaskLog.getPcdUpdatedTask(), taskUpdateReportReqDto);
            taskUpdateReportReqDto.setTaskID(""+taskUniqueId);
            updateTasks.put(taskUniqueId, taskUpdateReportReqDto);
        }

        generateReportReqDto.setUpdateReportList(new ArrayList<>(updateTasks.values()));

        GenerateReportResDto generateReportResDto = new GenerateReportResDto();

        try{
            generateReportResDto =  generateReportMsgSender.sendGenerateReportMessage("direct-exchange", "generate-report", generateReportReqDto);
        }catch (Exception e){
            e.printStackTrace();
        }

        return Arrays.asList(generateReportResDto);
    }

}