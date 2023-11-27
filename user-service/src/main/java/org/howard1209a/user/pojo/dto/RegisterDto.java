package org.howard1209a.user.pojo.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private Long userId;
    private String userName;
    private String password;
    private String email;
    private String identifyImageNumber;
}
