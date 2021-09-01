package com.presbo.presboservice.entity;

import java.util.List;

import javax.persistence.*;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Organisation {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Integer userLimit;

    @OneToMany(mappedBy="organisation")
    private List<Project> projects;

    @OneToMany(mappedBy="organisation", cascade = CascadeType.ALL)
    private List<User> users;

}