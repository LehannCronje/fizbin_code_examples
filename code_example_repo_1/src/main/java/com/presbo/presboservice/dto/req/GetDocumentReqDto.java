package com.presbo.presboservice.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class GetDocumentReqDto {

    private Long documentId;
    private List<String> fieldNames;

}
