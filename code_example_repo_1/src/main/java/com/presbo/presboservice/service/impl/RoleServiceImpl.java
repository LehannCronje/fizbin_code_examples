package com.presbo.presboservice.service.impl;

import com.presbo.presboservice.entity.Role;
import com.presbo.presboservice.repository.RoleRepository;
import com.presbo.presboservice.service.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl  implements RoleService{

    @Autowired
    RoleRepository roleRepo;

    @Override
    public Role findRoleByName(String roleName) {
        return roleRepo.findByName(roleName).get();
    }
}