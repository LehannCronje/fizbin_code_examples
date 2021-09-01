package com.presbo.presboservice.service.impl;

import com.presbo.presboservice.dto.req.OrganisationReqDto;
import com.presbo.presboservice.dto.res.OrganisationResDto;
import com.presbo.presboservice.entity.Organisation;
import com.presbo.presboservice.entity.User;
import com.presbo.presboservice.repository.OrganisationRepository;
import com.presbo.presboservice.service.OrganisationService;

import com.presbo.presboservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OrganisationServiceImpl implements OrganisationService {

    @Autowired
    private OrganisationRepository organisationRepo;

    @Autowired
    private UserService userService;

    @Override
    public Organisation findOrgById(Long organisationId) {
        return organisationRepo.findById(organisationId).get();
    }

    @Override
    public void saveOrganisation(Organisation organisation) {
        organisationRepo.save(organisation);
    }

    @Override
    public OrganisationResDto createOrganisation(OrganisationReqDto organisationReqDto, String username) {

        Organisation organisation = new Organisation();
        User user = userService.findUserByUsername(username);

        organisation.setName(organisationReqDto.getName());
        organisation.setUserLimit(5);
        user.setOrganisation(organisation);
        this.saveOrganisation(organisation);

        userService.saveUser(user);

        OrganisationResDto organisationResDto = new OrganisationResDto();
        organisationResDto.setId(organisation.getId());

        return organisationResDto;
    }
}