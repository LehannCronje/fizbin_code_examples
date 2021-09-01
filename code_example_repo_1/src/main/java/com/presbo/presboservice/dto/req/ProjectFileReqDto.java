package com.presbo.presboservice.dto.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProjectFileReqDto {

    private MultipartFile file;

}