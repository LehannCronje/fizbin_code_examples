package com.presbo.presboservice.dto.req;

import lombok.Data;

@Data
public class UserReqDto {

    private String username;
    private String password;
    private String role;
    private Long orgId;

}