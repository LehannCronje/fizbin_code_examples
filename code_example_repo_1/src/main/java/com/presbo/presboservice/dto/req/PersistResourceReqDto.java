package com.presbo.presboservice.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class PersistResourceReqDto {
    
    private Long projectId;
    private Long uniqueId;
    private String name;

    private List<PersistTaskReqDto> tasks;

}