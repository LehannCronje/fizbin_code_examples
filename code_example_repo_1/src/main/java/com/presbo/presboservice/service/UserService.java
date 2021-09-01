package com.presbo.presboservice.service;

import com.presbo.presboservice.dto.req.UserReqDto;
import com.presbo.presboservice.dto.res.UserResDto;
import com.presbo.presboservice.entity.User;

import java.util.List;

public interface UserService {

    User findUserByUsername(String username);

    void saveUser(User user);

    void createUser(UserReqDto userReqDto);

    List<UserResDto> getAllMobileUsersByOrganisation(Long organisationId);

    //Where should this be
    void addResources(List<Long> addedResources, String username);

    void removeResources(List<Long> removedResources, String username);

    void enableUser(String username);

    void disableUser(String username);

    void changePassword(UserReqDto userReqDto);
}