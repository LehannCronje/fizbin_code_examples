package com.presbo.presboservice.dto.res;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectResDto {

    private String id;

    private String name;

    private Boolean isLocked;

    private Date statusDate;

    private String fileName;

    private Date uploadDate;
    
}