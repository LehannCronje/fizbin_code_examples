package com.presbo.presboservice.repository;

import com.presbo.presboservice.entity.AssignedResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignedResourceRepository extends JpaRepository<AssignedResource, Long> {

    @Query(value = "select ar from AssignedResource ar where ar.resourceId = ?1 AND ar.user.id = ?2")
    Optional<AssignedResource> findByResourceIdAndUserId(Long resourceId, Long userId);

    Optional<AssignedResource> findByResourceId(Long resourceId);

}
