package org.howard1209a.blog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Img {
    private Long imgId;
    private Long userId;
    private String imgName;
    private Long imgGroupId;
    private String description;
    private Timestamp createTime;
    private String imgType;
}
