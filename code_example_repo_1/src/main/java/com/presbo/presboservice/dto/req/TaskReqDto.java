package com.presbo.presboservice.dto.req;

import lombok.Data;

@Data
public class TaskReqDto {

    private Long uniqueId;
    private String wbs;
    private String durationComplete;
    private String name;
    private Long parentTaskId;
    private String percentageComplete;
    private String remainingDuration;
    private String startDate;
    private String finishDate;
    private String isUpdated;
    private String isStarted;

}