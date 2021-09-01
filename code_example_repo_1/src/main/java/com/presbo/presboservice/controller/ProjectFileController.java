package com.presbo.presboservice.controller;

import com.presbo.presboservice.dto.req.UploadDocumentReqDto;
import com.presbo.presboservice.service.ProjectFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@CrossOrigin(origins = "*")
@RestController
public class ProjectFileController {
    

    @Autowired
    private ProjectFileService projectFileServiceImpl;

    @PostMapping("/upload-document")
    public String uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {

        UploadDocumentReqDto uploadDocumentReqDto = new UploadDocumentReqDto();
        uploadDocumentReqDto.setDocumentByteArray(file.getBytes());
        uploadDocumentReqDto.setDocumentName(file.getOriginalFilename());

        if(projectFileServiceImpl.uploadDocument(uploadDocumentReqDto, Long.valueOf(""+1)) != null){
            return "project File successfully uploaded";
        }

        return "A error has occurred";
    }

}