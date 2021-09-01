package com.presbo.presboservice.controller;

import com.presbo.presboservice.dto.req.GenerateReportReqDto;
import com.presbo.presboservice.dto.req.GetReportReqDto;
import com.presbo.presboservice.dto.req.TaskUpdateReportReqDto;
import com.presbo.presboservice.dto.res.GenerateReportResDto;
import com.presbo.presboservice.service.ProjectFileService;
import com.presbo.presboservice.service.ProjectReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/report")
public class ProjectReportController {

    @Autowired
    private ProjectReportService projectReportService;

    @Autowired
    private ProjectFileService projectFileService;

    @PostMapping("/generate-report/tasklistreport")
    public void generateTaskListReport(@RequestBody GenerateReportReqDto generateReportReqDto, HttpServletResponse response) {

        List<Long> reportDocumentIdList =  new ArrayList<>();

        for(GenerateReportResDto generateReportResDto : projectReportService.generateReport(generateReportReqDto)){
            reportDocumentIdList.add(generateReportResDto.getDocumentId());
        }

        List<File> filesToDelete = projectFileService.downloadAndZipDocuments(response, reportDocumentIdList);

        projectFileService.deleteFiles(filesToDelete);

    }

    @GetMapping("/generate-report/updatetaskreport/{projectId}")
    public void generateUpdateTaskReport(@PathVariable("projectId") Long projectId, HttpServletResponse response) {

        GenerateReportReqDto generateReportReqDto = new GenerateReportReqDto();
        List<Long> reportDocumentIdList =  new ArrayList<>();

        generateReportReqDto.setType("updateTask");
        generateReportReqDto.setProjectId(projectId);

        for(GenerateReportResDto generateReportResDto : projectReportService.generateReport(generateReportReqDto)){
            reportDocumentIdList.add(generateReportResDto.getDocumentId());
        }

        projectFileService.downloadAndZipDocuments(response, reportDocumentIdList);

        List<File> filesToDelete = projectFileService.downloadAndZipDocuments(response, reportDocumentIdList);

        projectFileService.deleteFiles(filesToDelete);

    }

    @GetMapping("/get-report")
    public String getReportMapping() {

        GetReportReqDto getReportReqDto = new GetReportReqDto();

        getReportReqDto.setReportId(1L);

       return projectReportService.getReport(getReportReqDto).toString();

    }

}