package com.presbo.presboservice.entity;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
public class UserRole {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
    
}