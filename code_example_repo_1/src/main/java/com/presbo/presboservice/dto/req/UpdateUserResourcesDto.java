package com.presbo.presboservice.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class UpdateUserResourcesDto {

    List<Long> removedResources;
    List<Long> addedResources;
    String username;

}
