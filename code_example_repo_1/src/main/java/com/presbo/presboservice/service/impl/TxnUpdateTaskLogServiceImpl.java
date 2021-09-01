package com.presbo.presboservice.service.impl;

import com.presbo.presboservice.dto.req.TxnUpdateTaskLogReqDto;
import com.presbo.presboservice.entity.Project;
import com.presbo.presboservice.entity.TxnUpdateTaskLog;
import com.presbo.presboservice.repository.TxnUpdateTaskLogRepository;
import com.presbo.presboservice.service.ProjectService;
import com.presbo.presboservice.service.TxnUpdateTaskLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TxnUpdateTaskLogServiceImpl implements TxnUpdateTaskLogService{

    @Autowired
    TxnUpdateTaskLogRepository txnUpdateTaskLogRepo;

    @Autowired
    ProjectService projectService;

    @Override
    public void insertTxnUpdateTaskLog(TxnUpdateTaskLog txnUpdateTaskLog) {

        txnUpdateTaskLogRepo.save(txnUpdateTaskLog);

    }

    @Override
    public List<TxnUpdateTaskLog> getAllTaskUpdateLogs(Long projectId) {

        Project project = projectService.findProjectById(projectId);

        return project.getTxnUpdateTaskLogs();


    }
}