package com.presbo.presboservice.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@SQLDelete(sql = "UPDATE project SET delete_date = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "delete_date IS NULL")
@Entity
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Boolean isLocked;
    private Date delete_date;
    private Date statusDate;
    private Long currentFileId;
    private Long createdAt;
    private Date updatedAt;

    @ManyToOne
    private Organisation organisation;

    @OneToMany(mappedBy = "project")
    private List<ProjectReport> projectReport;

    @OneToMany(mappedBy = "project")
    private List<ProjectFile> projectFile;

    @OneToMany(mappedBy = "project")
    private List<Resource> resources;

    @OneToMany(mappedBy = "project")
    private List<TxnUpdateTaskLog> txnUpdateTaskLogs;

    @OneToOne(mappedBy = "project")
    private Gallery gallery;
}