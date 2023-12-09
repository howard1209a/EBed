package org.howard1209a.blog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
