package com.presbo.presboservice.controller;

import com.presbo.presboservice.dto.res.ImageResDto;
import com.presbo.presboservice.service.ImageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    ImageService imageService;

    @GetMapping(value= "/{url}", produces = MediaType.IMAGE_JPEG_VALUE)
    public String getImage(@PathVariable("url") String imageUrl) throws Exception {
        String base64 = imageService.getImage(imageUrl);

        return base64;
    }

    @GetMapping("/gallery/{galleryId}")
    public List<ImageResDto> getImages(@PathVariable("galleryId") Long galleryId){

        return imageService.getImages(galleryId).stream().map(i -> {
            ImageResDto imageResDto = new ImageResDto();
            BeanUtils.copyProperties(i, imageResDto);
            return imageResDto;
        }).collect(toList());

    }
}
