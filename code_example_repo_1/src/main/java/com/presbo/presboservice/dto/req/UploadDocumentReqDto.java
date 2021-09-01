package com.presbo.presboservice.dto.req;

import lombok.Data;

@Data
public class UploadDocumentReqDto {

    private String documentName;
    private byte[] documentByteArray;
    private Long size;

}
