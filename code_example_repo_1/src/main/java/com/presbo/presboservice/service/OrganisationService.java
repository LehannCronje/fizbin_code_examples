package com.presbo.presboservice.service;

import com.presbo.presboservice.dto.req.OrganisationReqDto;
import com.presbo.presboservice.dto.res.OrganisationResDto;
import com.presbo.presboservice.entity.Organisation;

public interface OrganisationService {

    Organisation findOrgById(Long organisationId);

    void saveOrganisation(Organisation organisation);

    OrganisationResDto createOrganisation(OrganisationReqDto organisationReqDto, String username);
}