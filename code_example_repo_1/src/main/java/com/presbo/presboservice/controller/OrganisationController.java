package com.presbo.presboservice.controller;

import com.presbo.presboservice.dto.req.OrganisationReqDto;
import com.presbo.presboservice.dto.res.OrganisationResDto;
import com.presbo.presboservice.service.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("organisation")
public class OrganisationController {

    @Autowired
    OrganisationService organisationService;

    @PostMapping("/create")
    public OrganisationResDto createOrganisation(@RequestBody OrganisationReqDto organisationReqDto,@AuthenticationPrincipal UserDetails userDetails){

        return organisationService.createOrganisation(organisationReqDto, userDetails.getUsername());

    }

}