package com.presbo.presboservice.dto.res;

import lombok.Data;

import java.util.Date;

@Data
public class ImageResDto {

    private Long id;
    private String name;
    private String url;
    private Date createdAt;
    private String createdBy;

}
