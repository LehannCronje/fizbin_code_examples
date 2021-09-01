package com.presbo.presboservice.service;

import com.presbo.presboservice.dto.res.GalleryResDto;
import com.presbo.presboservice.entity.Gallery;

import java.util.List;

public interface GalleryService {

    List<GalleryResDto> getGalleries(Long OrgId);

    void saveGallery(Gallery gallery);

    Gallery getGallery(Long galleryId);

}
