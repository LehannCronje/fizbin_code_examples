package com.presbo.presboservice.service.impl;

import com.presbo.presboservice.dto.req.UserReqDto;
import com.presbo.presboservice.dto.res.UserResDto;
import com.presbo.presboservice.entity.*;
import com.presbo.presboservice.repository.AssignedResourceRepository;
import com.presbo.presboservice.repository.UserRepository;
import com.presbo.presboservice.service.OrganisationService;
import com.presbo.presboservice.service.RoleService;
import com.presbo.presboservice.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepo;

    @Autowired
    OrganisationService organisationService;

    @Autowired
    RoleService roleService;

    @Autowired
    AssignedResourceRepository assignedResourceRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public User findUserByUsername(String username) {
        return userRepo.findByUsername(username).get();
    }

    @Override
    public void saveUser(User user) {
        userRepo.save(user);
    }

    @Override
    public void createUser(UserReqDto userReqDto) {

        User user = new User();
        Role role = roleService.findRoleByName(userReqDto.getRole());
        UserRole userRole = new UserRole();

        userRole.setUser(user);
        userRole.setRole(role);

        user.setUserRoles(new ArrayList<>());
        List<UserRole> userRoles = user.getUserRoles();
        userRoles.add(userRole);

        if(userReqDto.getOrgId() != null){
            Organisation organisation = organisationService.findOrgById(userReqDto.getOrgId());
            user.setOrganisation(organisation);
        }
        
        user.setIsActive(true);
        user.setIsUpdate(false);
        user.setPassword(this.passwordEncoder.encode(userReqDto.getPassword()));
        user.setUsername(userReqDto.getUsername());
        user.setUserRoles(Arrays.asList(userRole));

        userRepo.save(user);
    }

    @Override
    public List<UserResDto> getAllMobileUsersByOrganisation(Long organisationId) {

        Organisation organisation = organisationService.findOrgById(organisationId);
        List<UserResDto> userResList = new ArrayList<>();
        UserResDto userResDto = new UserResDto();
        for(User user : organisation.getUsers()){
            if(user.getRoles().contains("ROLE_MOBILE")){
                userResDto = new UserResDto();
                userResDto.setId(user.getId());
                //All user resources should be added in this list;
                userResDto.setResources(new ArrayList<>());
                //Should return the list of roles to be displayed in the front-end
                userResDto.setRole(user.getRoles());
                userResDto.setIsActive(user.getIsActive());
                userResDto.setUsername(user.getUsername());
                userResList.add(userResDto);
            }
        }
        return userResList;
    }

    @Override
    public void addResources(List<Long> addedResources, String username) {

        User user = userRepo.findByUsername(username).get();
        AssignedResource assignedResource = new AssignedResource();

        if(user.getAssignedResources().isEmpty()){
            user.setAssignedResources(new ArrayList<>());
        }

        List<AssignedResource> assignedResources = user.getAssignedResources();

        for(Long resourceId : addedResources){
            assignedResource = assignedResourceRepo.findByResourceIdAndUserId(resourceId, user.getId()).orElse(null);
            if(assignedResource == null){
                assignedResource = new AssignedResource();
                assignedResource.setResourceId(resourceId);
                assignedResource.setUser(user);
                assignedResources.add(assignedResource);
            }
        }

        user.setAssignedResources(assignedResources);

        userRepo.save(user);


    }

    @Override
    public void removeResources(List<Long> removedResources, String username) {

        User user = userRepo.findByUsername(username).get();
        AssignedResource assignedResource;

        List<AssignedResource> assignedResources = user.getAssignedResources();

        for(Long resourceId : removedResources){
            assignedResource = assignedResourceRepo.findByResourceIdAndUserId(resourceId, user.getId()).orElse(null);
            if(assignedResource != null) {
                assignedResources.remove(assignedResource);
                assignedResourceRepo.delete(assignedResource);
            }
        }
        user.setAssignedResources(assignedResources);

        userRepo.save(user);

    }

    @Override
    public void enableUser(String username) {
        User user = this.findUserByUsername(username);

        user.setIsActive(true);

        userRepo.save(user);
    }

    @Override
    public void disableUser(String username) {
        User user = this.findUserByUsername(username);

        user.setIsActive(false);

        userRepo.save(user);
    }

    @Override
    public void changePassword(UserReqDto userReqDto) {

        User user = userRepo.findByUsername(userReqDto.getUsername()).get();

        user.setPassword(this.passwordEncoder.encode(userReqDto.getPassword()));

        userRepo.save(user);
    }
}