package com.presbo.presboservice.service.impl;

import com.presbo.presboservice.dto.req.GetDocumentReqDto;
import com.presbo.presboservice.dto.res.GetDocumentResDto;
import com.presbo.presboservice.entity.Gallery;
import com.presbo.presboservice.entity.Image;
import com.presbo.presboservice.messaging.sender.GetDocumentMsgSender;
import com.presbo.presboservice.repository.ImageRepository;
import com.presbo.presboservice.service.GalleryService;
import com.presbo.presboservice.service.ImageService;
import com.presbo.presboservice.service.ProjectFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    GalleryService galleryService;

    @Autowired
    ImageRepository imageRepo;

    @Autowired
    ProjectFileService projectFileService;

    @Autowired
    GetDocumentMsgSender getDocumentMsgSender;

    @Override
    public List<Image> getImages(Long galleryId) {
        Gallery gallery = galleryService.getGallery(galleryId);
        return gallery.getImageList();
    }

    @Override
    public void saveImage(Image image) {
        imageRepo.save(image);
    }

    @Override
    public String getImage(String imageUrl) throws Exception {

        Image image = this.getImageByUrl(imageUrl);

        GetDocumentReqDto getDocumentReqDto = new GetDocumentReqDto();
        getDocumentReqDto.setDocumentId(image.getProjectFileId());
        getDocumentReqDto.setFieldNames(Arrays.asList("base64"));


        GetDocumentResDto getDocumentResDto = getDocumentMsgSender.getDocumentMessage("direct-exchange", "get-document", getDocumentReqDto);

        return getDocumentResDto.getBase64File();
    }

    private Image getImageByUrl(String imageUrl){
        System.out.println(imageUrl);
        return imageRepo.findByName(imageUrl).orElseGet(null);
    }
}
