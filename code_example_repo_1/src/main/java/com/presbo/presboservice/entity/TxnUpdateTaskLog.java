package com.presbo.presboservice.entity;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
public class TxnUpdateTaskLog {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "txnUpdateTaskLog", cascade = CascadeType.ALL)
    private PcdUpdatedTask pcdUpdatedTask;

    @ManyToOne
    private Task task;

    @ManyToOne
    private Project project;

}