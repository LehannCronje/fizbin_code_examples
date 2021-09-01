package com.presbo.presboservice.dto.req;

import lombok.Data;

@Data
public class TaskUpdateReportReqDto {

    private String start;
    private String started;
    private String finish;
    private String finished;
    private String requireMoreWork;
    private String changeRemainingDuration;
    private String remainingDuration;
    private String notes;
    private String taskID;
    private String taskName;
    private Long projectId;

}
