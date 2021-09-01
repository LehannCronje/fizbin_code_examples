package com.presbo.presboservice.controller;

import com.presbo.presboservice.dto.res.AssignedResourceDto;
import com.presbo.presboservice.dto.res.ResourceResDto;
import com.presbo.presboservice.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    ResourceService resourceService;

    @GetMapping("/{projectId}")
    public List<ResourceResDto> getResources(@PathVariable("projectId") Long projectId){

        return resourceService.getAllResourcesByProject(projectId);

    }

    //should this be in the resource controller
    @GetMapping("/assigned-resources/{username}")
    public List<AssignedResourceDto> getAssignedResources(@PathVariable("username") String username){

        return resourceService.findAssignedResources(username);

    }
    
}