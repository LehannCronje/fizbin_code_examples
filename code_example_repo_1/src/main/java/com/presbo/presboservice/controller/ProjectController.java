package com.presbo.presboservice.controller;

import com.presbo.presboservice.dto.req.ProjectReqDto;
import com.presbo.presboservice.dto.res.ProjectResDto;
import com.presbo.presboservice.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @PostMapping("/create")
    public void createProject(@RequestBody ProjectReqDto projectReqDto){

        projectService.createProject(projectReqDto);

    }

    @PostMapping("/populate")
    public void populateProject(@RequestParam("projectFile") MultipartFile file, @RequestParam("projectId") Long projectId){
        ProjectReqDto projectReqDto = new ProjectReqDto();
        projectReqDto.setProjectFile(file);
        projectReqDto.setProjectId(projectId);

        projectService.persistExtractedProjectData(projectService.extractProject(projectReqDto));

    }

    @PostMapping("/update")
    public void updateProject(@RequestParam("projectFile") MultipartFile file, @RequestParam("projectId") Long projectId){

        ProjectReqDto projectReqDto = new ProjectReqDto();
        projectReqDto.setProjectFile(file);
        projectReqDto.setProjectId(projectId);

        projectService.updateProject(projectReqDto);

    }


    //Should this be in the project Controller
    @GetMapping("/{orgId}")
    public List<ProjectResDto> getAllProjects(@PathVariable("orgId") Long organisationId) throws Exception {

        return projectService.getAllProjects(organisationId);

    }

    @DeleteMapping("/delete/{projectId}")
    public void deleteProject(@PathVariable("projectId") Long projectId){
        projectService.deleteProject(projectId);
    }

    @GetMapping("/lock/{projectId}")
    public void lockProject(@PathVariable("projectId") Long projectId){
        projectService.lockProject(projectId);
    }

    @GetMapping("/unlock/{projectId}")
    public void unlockProject(@PathVariable("projectId") Long projectId){
        projectService.unlockProject(projectId);
    }
}