package com.presbo.presboservice.service.impl;

import com.presbo.presboservice.dto.res.GalleryResDto;
import com.presbo.presboservice.entity.Gallery;
import com.presbo.presboservice.entity.Organisation;
import com.presbo.presboservice.entity.Project;
import com.presbo.presboservice.repository.GalleryRepository;
import com.presbo.presboservice.service.GalleryService;
import com.presbo.presboservice.service.OrganisationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class GalleryServiceImpl implements GalleryService {

    @Autowired
    OrganisationService orgService;

    @Autowired
    GalleryRepository galleryRepo;

    @Override
    public List<GalleryResDto> getGalleries(Long orgId) {
        Organisation organisation = orgService.findOrgById(orgId);
        return organisation.getProjects().stream().map(p -> {
            GalleryResDto galleryResDto = new GalleryResDto();
            BeanUtils.copyProperties(p.getGallery(), galleryResDto);
            return galleryResDto;
        }).collect(toList());
    }

    @Override
    public void saveGallery(Gallery gallery) {
        galleryRepo.save(gallery);
    }

    @Override
    public Gallery getGallery(Long galleryId) {
        return galleryRepo.findById(galleryId).get();
    }
}
