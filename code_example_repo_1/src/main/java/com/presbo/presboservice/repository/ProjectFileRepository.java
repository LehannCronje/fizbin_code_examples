package com.presbo.presboservice.repository;

import com.presbo.presboservice.entity.ProjectFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long>{
    
}