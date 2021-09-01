package com.presbo.presboservice.repository;

import com.presbo.presboservice.entity.TxnUpdateTaskLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TxnUpdateTaskLogRepository extends JpaRepository<TxnUpdateTaskLog, Long>{
    
}