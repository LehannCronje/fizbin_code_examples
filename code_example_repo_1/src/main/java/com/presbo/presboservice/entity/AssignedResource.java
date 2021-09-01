package com.presbo.presboservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AssignedResource {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private Long resourceId;

}
