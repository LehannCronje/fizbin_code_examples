package com.presbo.presboservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Gallery {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Date updatedAt;

    @OneToOne
    Project project;

    @OneToMany(mappedBy = "gallery")
    private List<Image> imageList;
}
