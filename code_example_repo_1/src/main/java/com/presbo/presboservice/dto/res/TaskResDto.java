package com.presbo.presboservice.dto.res;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TaskResDto implements Comparable<TaskResDto>{

    //database Id
    private Long id;
    //Projectfile task unique Id
    private Long uniqueId;
    //Projectfile task id
    private Long taskId;
    private Long fileId;
    private String wbs;
    private String durationComplete;
    private String name;
    private Long parentTaskId;
    private String parentTaskName;
    private String parentTaskWbs;
    private String percentageComplete;
    private String remainingDuration;
    private Date startDate;
    private Date finishDate;
    private Boolean isUpdated;
    private Boolean isStarted;
    private String notes;
    private TxnUpdateTaskLogResDto txnUpdateTaskLogResDto;

    private List<TaskResDto> taskList;

    @Override
    public int compareTo(TaskResDto o) {
        if(o.getUniqueId() == null){
            System.out.println("object: " + o.toString());
        }
        if(this.getUniqueId() == null){
            System.out.println("this: " + this);
        }
        return this.getUniqueId().compareTo(o.getUniqueId());
    }

    public void setIsStarted(Boolean isStarted) {
        this.isStarted = isStarted;
    }
}