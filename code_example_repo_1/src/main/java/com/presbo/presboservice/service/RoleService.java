package com.presbo.presboservice.service;

import com.presbo.presboservice.entity.Role;

public interface RoleService {

    Role findRoleByName(String roleName);

}