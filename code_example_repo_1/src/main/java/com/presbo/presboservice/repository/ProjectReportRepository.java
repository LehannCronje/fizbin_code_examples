package com.presbo.presboservice.repository;

import com.presbo.presboservice.entity.ProjectReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectReportRepository extends JpaRepository<ProjectReport, Long> {
}
