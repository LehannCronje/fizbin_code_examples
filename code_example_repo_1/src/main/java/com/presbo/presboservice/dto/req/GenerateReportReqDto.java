package com.presbo.presboservice.dto.req;

import com.presbo.presboservice.dto.res.TaskResDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GenerateReportReqDto {

    private String type;

    private String projectName;

    private Date projectStatusDate;

    private String resourceName;

    private String date;

    private List<Long> resourceIds;

    private List<TaskResDto> taskList;

    private Long projectId;

    private TaskListFilterReqDto filterData;

    private List<TaskUpdateReportReqDto> updateReportList;
}
