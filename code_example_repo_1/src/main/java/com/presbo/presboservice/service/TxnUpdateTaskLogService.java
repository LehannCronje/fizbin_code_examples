package com.presbo.presboservice.service;

import com.presbo.presboservice.dto.req.TxnUpdateTaskLogReqDto;
import com.presbo.presboservice.entity.TxnUpdateTaskLog;

import java.util.List;

public interface TxnUpdateTaskLogService {

    void insertTxnUpdateTaskLog(TxnUpdateTaskLog txnUpdateTaskLog);

    List<TxnUpdateTaskLog> getAllTaskUpdateLogs(Long projectId);

}