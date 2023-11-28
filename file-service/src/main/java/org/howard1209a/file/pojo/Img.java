package org.howard1209a.file.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Img {
    private long imgId;
    private long userId;
    private String imgName;
    private long groupId;
    private String description;
    private Timestamp createTime;
}
