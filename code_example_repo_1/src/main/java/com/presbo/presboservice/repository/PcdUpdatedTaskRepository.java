package com.presbo.presboservice.repository;

import com.presbo.presboservice.entity.PcdUpdatedTask;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PcdUpdatedTaskRepository extends JpaRepository<PcdUpdatedTask, Long>{
    
}