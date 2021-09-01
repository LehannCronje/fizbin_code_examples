package com.presbo.presboservice.dto.req;

import lombok.Data;

import java.util.Date;

@Data
public class TaskListFilterReqDto {

    private Date startDate;

    private Date endDate;

    private Boolean excludeMilestoneTasks;

}
