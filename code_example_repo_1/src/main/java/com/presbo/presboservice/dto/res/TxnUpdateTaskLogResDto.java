package com.presbo.presboservice.dto.res;

import lombok.Data;

@Data
public class TxnUpdateTaskLogResDto {

    private String id;
    private Boolean isStarted;
    private String start;
    private Boolean isFinished;
    private String finish;
    private Boolean changeRemainingDuration;
    private String remainingDuration;
    private Boolean requireMoreWork;
    private String notes;

}