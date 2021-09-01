package com.presbo.presboservice.service;

import com.presbo.presboservice.dto.req.UploadDocumentReqDto;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

public interface ProjectFileService {
    
    public Long uploadDocument(UploadDocumentReqDto uploadDocumentReqDto, Long projectId);

    List<File> downloadAndZipDocuments(HttpServletResponse response, List<Long> documentIds);

    void deleteFiles(List<File> filesToDelete);
}