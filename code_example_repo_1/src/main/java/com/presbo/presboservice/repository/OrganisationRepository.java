package com.presbo.presboservice.repository;

import com.presbo.presboservice.entity.Organisation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    
}