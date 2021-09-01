package com.presbo.presboservice.dto.res;

import lombok.Data;

import java.util.List;

@Data
public class UserResDto {

    private Long id;
    private String username;
    private List<String> role;
    private Boolean isActive;
    private List<ResourceResDto> resources;

}