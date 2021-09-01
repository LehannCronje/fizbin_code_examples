package com.presbo.presboservice.entity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static java.util.stream.Collectors.toList;

@Entity
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long Id;

    @Column(unique = true)
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    private Boolean isActive;

    private Boolean isUpdate;
    
    @ManyToOne
    private Organisation organisation;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "user")
    private List<UserRole> userRoles;

    //better restriction solution must be found.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AssignedResource> assignedResources;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream().map(SimpleGrantedAuthority::new).collect(toList());
    }

    public List<String> getRoles(){
        return this.userRoles.stream().map(UserRole::getRole).map(Role::getName).collect(toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (this.isActive == null)
            return false;
        if (!this.isActive)
            return false;

        return true;
    }
}