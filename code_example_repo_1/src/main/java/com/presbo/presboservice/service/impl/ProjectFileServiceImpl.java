package com.presbo.presboservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.presbo.presboservice.dto.req.GetDocumentReqDto;
import com.presbo.presboservice.dto.req.UploadDocumentReqDto;
import com.presbo.presboservice.dto.res.GetDocumentResDto;
import com.presbo.presboservice.dto.res.UploadDocumentResDto;
import com.presbo.presboservice.entity.Project;
import com.presbo.presboservice.entity.ProjectFile;
import com.presbo.presboservice.messaging.sender.GetDocumentMsgSender;
import com.presbo.presboservice.messaging.sender.UploadDocumentMsgSender;
import com.presbo.presboservice.repository.ProjectFileRepository;
import com.presbo.presboservice.service.ProjectFileService;
import com.presbo.presboservice.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ProjectFileServiceImpl implements ProjectFileService {

    @Autowired
    UploadDocumentMsgSender uploadDocumentMsgSender;

    @Autowired
    GetDocumentMsgSender getDocumentMsgSender;

    @Autowired
    ProjectFileRepository projectFileRepo;

    @Autowired
    ProjectService projectService;


    @Override
    public Long uploadDocument(UploadDocumentReqDto uploadDocumentReqDto, Long projectId) {

        try {
            UploadDocumentResDto uploadDocumentResDto = uploadDocumentMsgSender.sendUploadMessage("direct-exchange", "upload-document", uploadDocumentReqDto);
            Long projectFileId = uploadDocumentResDto.getDocumentId();
            ProjectFile projectFile = new ProjectFile();
            Project project = projectService.findProjectById(projectId);

            projectFile.setFileId(projectFileId);
            projectFile.setProject(project);
            projectFileRepo.save(projectFile);

            return projectFileId;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<File> downloadAndZipDocuments(HttpServletResponse response, List<Long> documentIds) {


        response.setContentType("application/octet-stream");
        response.setHeader("Access-Control-Allow-Headers",
                "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Content-Disposition", "attachment;filename=download.zip");
        response.setStatus(HttpServletResponse.SC_OK);

        String FILEPATH = "./temp/";

        File newDirectory = new File(FILEPATH);

        List<File> fileArrayList = new ArrayList<File>();

        if (!newDirectory.isDirectory()) {
            newDirectory.mkdir();
        }

        GetDocumentReqDto getDocumentReqDto;
        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            for (Long docId : documentIds) {

                getDocumentReqDto = new GetDocumentReqDto();
                getDocumentReqDto.setDocumentId(docId);


                GetDocumentResDto getDocumentResDto = getDocumentMsgSender.getDocumentMessage("direct-exchange", "get-document", getDocumentReqDto);

                byte[] fileByteArray = Base64.getDecoder().decode(getDocumentResDto.getBase64File());

                File file = new File(FILEPATH + getDocumentResDto.getName());

                OutputStream os = new FileOutputStream(file);

                os.write(fileByteArray);
                os.close();
                FileSystemResource resource = new FileSystemResource(file.getPath());

                ZipEntry e = new ZipEntry(resource.getFilename());
                // Configure the zip entry, the properties of the file
                e.setSize(resource.contentLength());
                e.setTime(System.currentTimeMillis());
                // etc.
                zippedOut.putNextEntry(e);
                // And the content of the resource:
                StreamUtils.copy(resource.getInputStream(), zippedOut);

                zippedOut.closeEntry();
                fileArrayList.add(file);

            }
            zippedOut.finish();
        } catch (Exception e) {
            e.printStackTrace();
            // Exception handling goes here
        }

        return fileArrayList;

    }

    @Override
    public void deleteFiles(List<File> filesToDelete) {

        for(File fileToDelete : filesToDelete){
            fileToDelete.delete();
        }

    }

}