package com.presbo.presboservice.service;

import com.presbo.presboservice.dto.req.TaskListFilterReqDto;
import com.presbo.presboservice.dto.res.TaskResDto;
import com.presbo.presboservice.entity.Task;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskService {

    void saveTask(Task task);

    Optional<Task> findTaskByUniqueId(Long uniqueId);

    Optional<Task> findTaskById(Long taskId);

    boolean deleteTasksByResource(List<Task> tasks);

    List<TaskResDto> getAllTasksByResourceId(Long resourceId, TaskListFilterReqDto taskListFilterData) throws ParseException;
}