package com.presbo.presboservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;


@Data
@Entity
public class ProjectReport {

    @Id
    @GeneratedValue
    private Long id;

    private Long reportId;

    @ManyToOne
    private Project project;

    
}