package com.presbo.presboservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Data
@Entity
public class PcdUpdatedTask {
    
    @Id
    @GeneratedValue
    private Long id;

    private Boolean changeRemainingDuration;
    private String finish;
    private Boolean isFinished;
    private String notes;
    private Long procesed;
    private String remainingDuration;
    private Boolean requireMoreWork;
    private String start;
    private Boolean isStarted;
    private String taskName;

    @OneToOne
    private TxnUpdateTaskLog txnUpdateTaskLog;

}