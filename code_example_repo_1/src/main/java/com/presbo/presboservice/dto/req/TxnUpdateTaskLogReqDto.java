package com.presbo.presboservice.dto.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class TxnUpdateTaskLogReqDto {

    public String start;
    public Boolean isStarted;
    public Boolean isFinished;
    public String finish;
    public Boolean requireMoreWork;
    public Boolean changeRemainingDuration;
    public String remainingDuration;
    public String notes;
    public Long resourceId;
    public Long taskId;
    public String taskName;
    public Long projectId;
    public String type;
    public List<String> files;



}