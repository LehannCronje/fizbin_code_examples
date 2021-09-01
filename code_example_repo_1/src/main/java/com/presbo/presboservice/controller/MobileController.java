package com.presbo.presboservice.controller;

import com.presbo.presboservice.dto.req.TxnUpdateTaskLogReqDto;
import com.presbo.presboservice.dto.res.ProjectResDto;
import com.presbo.presboservice.dto.res.ResourceResDto;
import com.presbo.presboservice.dto.res.TaskResDto;
import com.presbo.presboservice.entity.Image;
import com.presbo.presboservice.entity.Project;
import com.presbo.presboservice.entity.User;
import com.presbo.presboservice.service.ImageService;
import com.presbo.presboservice.service.MobileService;
import com.presbo.presboservice.service.ProjectService;
import com.presbo.presboservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/mobile")
public class MobileController {

    @Autowired
    MobileService mobileService;

    @Autowired
    UserService userService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ImageService imageService;

    @Autowired
    private ServletContext context;

    @GetMapping("project/all")
    public List<ProjectResDto> getAllProjectsForMobile(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response){

        if (!userService.findUserByUsername(userDetails.getUsername()).isEnabled()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return new ArrayList<>();
        }

        return mobileService.getAllMobileUserProjects(userDetails.getUsername());

    }

    @GetMapping("/project/resources/{projectId}")
    public List<ResourceResDto> getAllResourceForMobile(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("projectId") Long projectId, HttpServletResponse response, HttpServletRequest request){

        User user =userService.findUserByUsername(userDetails.getUsername());

        if (!user.isEnabled()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return new ArrayList<>();
        }

        if (projectService.findProjectById(Long.valueOf(request.getHeader("projectId"))).getIsLocked() || user.getIsUpdate()) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return new ArrayList<>();
        } else {
            return mobileService.getAllMobileResources(userDetails.getUsername(), projectId);
        }



    }

    @GetMapping("/project/resource/tasks/{resourceId}")
    public List<TaskResDto> getAllTasksForMobile(@PathVariable("resourceId") Long resourceId, HttpServletResponse response, HttpServletRequest request,@AuthenticationPrincipal UserDetails userDetails) throws ParseException {


        User user =userService.findUserByUsername(userDetails.getUsername());

        if (!user.isEnabled()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return new ArrayList<>();
        }

        if (projectService.findProjectById(Long.valueOf(request.getHeader("projectId"))).getIsLocked() || user.getIsUpdate()) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return new ArrayList<>();
        } else {
            return mobileService.getAllMobileTasksByResourceId(resourceId);
        }



    }

    @PostMapping("/task/update")
    public void updateTask(@RequestBody TxnUpdateTaskLogReqDto txnUpdateTaskLogReqDto, HttpServletResponse response, HttpServletRequest request,@AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User user =userService.findUserByUsername(userDetails.getUsername());

        if (!user.isEnabled()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

        if (projectService.findProjectById(Long.valueOf(request.getHeader("projectId"))).getIsLocked() || user.getIsUpdate()) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            mobileService.updateTask(txnUpdateTaskLogReqDto);
            mobileService.handleImages(txnUpdateTaskLogReqDto.getFiles(), txnUpdateTaskLogReqDto.getTaskId(), txnUpdateTaskLogReqDto.getProjectId());
        }


    }

    @GetMapping("/me")
    public ResponseEntity currentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Map<Object, Object> model = new HashMap<>();
        try {
            model.put("username", userDetails.getUsername());
            model.put("roles", userDetails.getAuthorities().stream().map(a -> ((GrantedAuthority) a).getAuthority())
                    .collect(toList()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok(model);
    }

}
