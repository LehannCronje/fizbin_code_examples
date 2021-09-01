package com.presbo.presboservice.service;

import com.presbo.presboservice.dto.req.PersistResourceReqDto;
import com.presbo.presboservice.dto.req.ProjectReqDto;
import com.presbo.presboservice.dto.res.ProjectResDto;
import com.presbo.presboservice.entity.Project;

import java.util.List;

public interface ProjectService {

    Project createProject(ProjectReqDto projectReqDto);

    List<PersistResourceReqDto> extractProject(ProjectReqDto projectReqDto);

    void persistExtractedProjectData(List<PersistResourceReqDto> persistData);

    void updateProject(ProjectReqDto projectReqDto);

    void deleteProject(Long projectId);

    void saveProject(Project project);

    Project findProjectById(Long projectId);

    List<ProjectResDto> getAllProjects(Long organisationId) throws Exception;

    void lockProject(Long projectId);

    void unlockProject(Long projectId);
}