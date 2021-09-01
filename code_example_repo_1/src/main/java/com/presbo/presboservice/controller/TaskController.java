package com.presbo.presboservice.controller;

import com.presbo.presboservice.dto.req.TaskListFilterReqDto;
import com.presbo.presboservice.dto.res.TaskResDto;
import com.presbo.presboservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    TaskService taskService;

    @GetMapping("/resource/{resourceId}/{startDate}/{endDate}/{filterMilestones}")
    public List<TaskResDto> getTasksByResourceId(@PathVariable("resourceId") Long resourceId, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate, @PathVariable Boolean filterMilestones) throws ParseException {

        TaskListFilterReqDto taskListFilterReqDto = new TaskListFilterReqDto();
        Date startDateDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
        Date endDateDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
        taskListFilterReqDto.setStartDate(startDateDate);
        taskListFilterReqDto.setEndDate(endDateDate);
        taskListFilterReqDto.setExcludeMilestoneTasks(filterMilestones);
        System.out.println(taskListFilterReqDto.getEndDate());
        return taskService.getAllTasksByResourceId(resourceId, taskListFilterReqDto);

    }

    @GetMapping("/project/{projectId}")
    public List<TaskResDto> getTasksByProjectId(@PathVariable("projectId") Long projectId){

//        return taskService.getAllTasksByProjectId(projectId);

        return new ArrayList<>();

    }

    @GetMapping("/")
    public String test(){

        return "true";

    }

}