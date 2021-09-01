package com.presbo.presboservice.dto.res;

import lombok.Data;
import org.springframework.core.io.FileSystemResource;

import java.util.Date;

@Data
public class GetDocumentResDto{

    private Long id;
    private String name;
    private String base64File;
    private Date uploadDate;
    private byte[] fileBytes;

}
