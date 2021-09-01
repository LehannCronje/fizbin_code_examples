package com.presbo.presboservice.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReqDto {

    private Long projectId;

    private MultipartFile projectFile;

    private Long organisationId;

    private String projectName;

}