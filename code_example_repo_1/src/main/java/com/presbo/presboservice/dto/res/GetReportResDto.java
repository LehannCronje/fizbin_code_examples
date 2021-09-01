package com.presbo.presboservice.dto.res;

import lombok.Data;

@Data
public class GetReportResDto {

    private Long reportId;
    private String reportName;
    private GetDocumentResDto getDocumentResDto;

}
