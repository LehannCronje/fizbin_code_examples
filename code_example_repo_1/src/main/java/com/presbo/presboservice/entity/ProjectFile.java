package com.presbo.presboservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

import java.util.Date;

@Data
@Entity
public class ProjectFile {

    @Id
    @GeneratedValue
    private Long id;

    private Long fileId;

    @ManyToOne
    private Project project;

}