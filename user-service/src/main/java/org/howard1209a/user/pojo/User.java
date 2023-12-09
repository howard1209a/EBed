package org.howard1209a.user.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.howard1209a.user.pojo.dto.RegisterDto;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long userId;
    private String userName;
    private String password;
    private String email;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer exp;
    private Integer funNum;
    private Integer followNum;
    private Integer likeNum;
    private String selfIntroduction;
    private String profilePhotoType;

    public User(RegisterDto registerDto) {
        userId = registerDto.getUserId();
        userName = registerDto.getUserName();
        password = registerDto.getPassword();
        email = registerDto.getEmail();
    }
}
