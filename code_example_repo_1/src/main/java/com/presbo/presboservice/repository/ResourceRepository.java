package com.presbo.presboservice.repository;

import com.presbo.presboservice.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>{

    Optional<Resource> findByUniqueIdAndProjectId(Long uniqueId, Long projectId);
    
}