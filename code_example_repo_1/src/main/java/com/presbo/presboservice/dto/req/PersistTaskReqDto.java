package com.presbo.presboservice.dto.req;

import lombok.Data;

import java.util.Date;

@Data
public class PersistTaskReqDto {

    private Long uniqueId;
    private Long taskId;
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
    private boolean isUpdated;
    private boolean isStarted;
    private String notes;

}
