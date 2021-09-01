package com.presbo.presboservice.service;

import com.presbo.presboservice.dto.req.TxnUpdateTaskLogReqDto;
import com.presbo.presboservice.dto.res.ProjectResDto;
import com.presbo.presboservice.dto.res.ResourceResDto;
import com.presbo.presboservice.dto.res.TaskResDto;

import java.text.ParseException;
import java.util.List;

public interface MobileService {

    List<ProjectResDto> getAllMobileUserProjects(String username);

    List<ResourceResDto> getAllMobileResources(String username, Long projectId);

    List<TaskResDto> getAllMobileTasksByResourceId(Long resourceId) throws ParseException;

    void updateTask(TxnUpdateTaskLogReqDto txnUpdateTaskLogReqDto);

    void handleImages(List<String> files, Long taskId, Long projectId);

}
