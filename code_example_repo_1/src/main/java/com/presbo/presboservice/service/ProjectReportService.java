package com.presbo.presboservice.service;

import com.presbo.presboservice.dto.req.GenerateReportReqDto;
import com.presbo.presboservice.dto.req.GetReportReqDto;
import com.presbo.presboservice.dto.res.GenerateReportResDto;
import com.presbo.presboservice.dto.res.GetReportResDto;

import java.util.List;

public interface ProjectReportService {

    public List<GenerateReportResDto> generateReport(GenerateReportReqDto generateReportReqDto);

    public GetReportResDto getReport(GetReportReqDto getReportReqDto);

}