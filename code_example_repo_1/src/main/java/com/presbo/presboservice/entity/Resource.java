package com.presbo.presboservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE resource SET delete_date = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "delete_date IS NULL")
@Entity
public class Resource {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Long uniqueId;
    private Date delete_date;

    @ManyToOne
    private Project project;

    @ManyToMany(mappedBy = "resources")
    private Set<Task> tasks;

    
}