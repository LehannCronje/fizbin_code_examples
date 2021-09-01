package com.presbo.presboservice.service;

import com.presbo.presboservice.entity.Image;

import java.util.List;

public interface ImageService {

    List<Image> getImages(Long galleryId);

    void saveImage(Image image);

    String getImage(String imageUrl) throws Exception;

}
