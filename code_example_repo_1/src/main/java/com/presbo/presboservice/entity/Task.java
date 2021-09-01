package com.presbo.presboservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE task SET delete_date = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "delete_date IS NULL")
@Entity
public class Task {

    //review for task hierarchy purposes
    @Id
    @GeneratedValue
    private Long id;

    private Long uniqueId;
    private Long taskId;
    private String wbs;
    private String durationComplete;
    private String name;
    private Long parentTaskId;
    private String parentTaskName;
    private String parentTaskWbs;
    private String percentageComplete;
    private String remainingDuration;
    private Date startDate;
    private Date finishDate;
    private Boolean isUpdated;
    private Boolean isStarted;
    private String notes;

    private Date delete_date;


    @ManyToMany
    @JoinTable(name="resource_task", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "resource_id"))
    private Set<Resource> resources;

    @OneToMany(mappedBy = "task")
    private List<TxnUpdateTaskLog> updateTaskLogs;

}