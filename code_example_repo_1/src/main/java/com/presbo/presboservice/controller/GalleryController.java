package com.presbo.presboservice.controller;

import com.presbo.presboservice.dto.res.GalleryResDto;
import com.presbo.presboservice.entity.Gallery;
import com.presbo.presboservice.service.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gallery")
public class GalleryController {

    @Autowired
    GalleryService galleryService;

    @GetMapping("/{orgId}")
    public List<GalleryResDto> getGalleries(@PathVariable("orgId") Long orgId){
        return galleryService.getGalleries(orgId);
    }

}
