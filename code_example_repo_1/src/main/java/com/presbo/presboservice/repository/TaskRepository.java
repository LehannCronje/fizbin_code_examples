package com.presbo.presboservice.repository;

import java.util.Optional;

import com.presbo.presboservice.entity.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    
    Optional<Task> findByUniqueId(Long uniqueId);

}