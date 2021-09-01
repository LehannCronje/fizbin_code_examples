package com.presbo.presboservice.service.impl;

import com.presbo.presboservice.dto.req.TaskListFilterReqDto;
import com.presbo.presboservice.dto.res.TaskResDto;
import com.presbo.presboservice.dto.res.TxnUpdateTaskLogResDto;
import com.presbo.presboservice.entity.PcdUpdatedTask;
import com.presbo.presboservice.entity.Project;
import com.presbo.presboservice.entity.Resource;
import com.presbo.presboservice.entity.Task;
import com.presbo.presboservice.repository.TaskRepository;
import com.presbo.presboservice.service.ProjectService;
import com.presbo.presboservice.service.ResourceService;
import com.presbo.presboservice.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepository taskRepo;

    @Autowired
    ProjectService projectService;

    @Autowired
    ResourceService resourceService;

    @Override
    public void saveTask(Task task) {
        taskRepo.save(task);
    }

    @Override
    public Optional<Task> findTaskByUniqueId(Long uniqueId) {
        return taskRepo.findByUniqueId(uniqueId);
    }

    @Override
    public Optional<Task> findTaskById(Long taskId) {
        return taskRepo.findById(taskId);
    }

    @Override
    public boolean deleteTasksByResource(List<Task> tasks) {

        taskRepo.deleteAll(tasks);
        return true;
    }

    @Override
    public List<TaskResDto> getAllTasksByResourceId(Long resourceId, TaskListFilterReqDto taskListFilterData) throws ParseException {

        Resource resource = resourceService.findResourceById(resourceId).get();
        System.out.println(resource.getTasks());
        List<TaskResDto> tasks = buildTaskHierarchy(new ArrayList<>(resource.getTasks()), taskListFilterData);
        Collections.sort(tasks);
        return tasks;
    }

    private List<TaskResDto> buildTaskHierarchy(List<Task> taskList, TaskListFilterReqDto taskListFilterData) throws ParseException {

        Map<Long, TaskResDto> taskHierarchyMap = new HashMap<Long, TaskResDto>();
        TaskResDto childTaskResDto = new TaskResDto();
        TaskResDto parentTaskResDto = new TaskResDto();

        for(Task task : taskList){

            if (testDate(taskListFilterData ,task.getStartDate()) && !task.getPercentageComplete().equals("100.0%") && includeMilstoneTasks(taskListFilterData, task)) {

                Long parentTaskId = task.getParentTaskId();

                if(taskHierarchyMap.containsKey(parentTaskId)){
                    parentTaskResDto = taskHierarchyMap.get(parentTaskId);
                }else{
                    parentTaskResDto = new TaskResDto();

                    //build parent
                    if(task.getParentTaskId()!= null){
                        parentTaskResDto.setName(task.getParentTaskName());
                        parentTaskResDto.setUniqueId(task.getParentTaskId());
                        parentTaskResDto.setWbs(task.getParentTaskWbs());
                        parentTaskResDto.setTaskList(new ArrayList<>());

                        taskHierarchyMap.put(parentTaskId, parentTaskResDto);
                    }

                }

                if(task.getParentTaskId()!=null){
                    //build child
                    childTaskResDto = this.buildHierarchyChildTask(task);

                    //add child to parent
                    parentTaskResDto.getTaskList().add(childTaskResDto);
                }


            }

        }

        return new ArrayList<>(taskHierarchyMap.values());

    }

    private TaskResDto buildHierarchyChildTask(Task task){

        TaskResDto taskResDto = new TaskResDto();
        
        taskResDto.setId(task.getId());
        taskResDto.setTaskId(task.getTaskId());
        taskResDto.setName(task.getName());
        taskResDto.setWbs(task.getWbs());
        taskResDto.setUniqueId(task.getUniqueId());
        taskResDto.setDurationComplete(task.getDurationComplete());
        taskResDto.setPercentageComplete(task.getPercentageComplete());
        taskResDto.setRemainingDuration(task.getRemainingDuration());
        taskResDto.setFinishDate(task.getFinishDate());
        taskResDto.setStartDate(task.getStartDate());
        taskResDto.setIsStarted(task.getIsStarted());
        taskResDto.setNotes(task.getNotes());
        taskResDto.setIsUpdated(task.getIsUpdated());

        if(!task.getUpdateTaskLogs().isEmpty()){

            PcdUpdatedTask pcdUpdatedTask = task.getUpdateTaskLogs().get(0).getPcdUpdatedTask();
            TxnUpdateTaskLogResDto txnUpdateTaskLogResDto = new TxnUpdateTaskLogResDto();

            BeanUtils.copyProperties(pcdUpdatedTask, txnUpdateTaskLogResDto);
            taskResDto.setTxnUpdateTaskLogResDto(txnUpdateTaskLogResDto);
        }


        return taskResDto;

    }


    //Date util methods
    public boolean testDate(TaskListFilterReqDto taskListFilterData, Date inputDate) throws ParseException {
        return inputDate.after(taskListFilterData.getStartDate()) && inputDate.before(taskListFilterData.getEndDate());

    }

    public Date getDateTwoWeeks(Date date, String calendarType, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(calendarType.equals("Hours")){
            calendar.add(Calendar.HOUR_OF_DAY, value);
        }
        if(calendarType.equals("Days")){
            calendar.add(Calendar.DATE, value);
        }
        if(calendarType.equals("Weeks")){
            calendar.add(Calendar.WEEK_OF_YEAR, value);
        }
        return calendar.getTime();
    }

    private Boolean includeMilstoneTasks(TaskListFilterReqDto taskListFilterReqDto, Task task){
        if(taskListFilterReqDto.getExcludeMilestoneTasks() != null){
            if(taskListFilterReqDto.getExcludeMilestoneTasks() && task.getRemainingDuration().equals("0.0d")){
                System.out.println(task.getRemainingDuration());
                return false;
            }
        } else {
            return true;
        }
        return false;
    }

}