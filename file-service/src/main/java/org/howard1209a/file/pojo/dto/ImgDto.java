package org.howard1209a.file.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImgDto {
    private String imgName;
    private String url;
    private String description;
    private Timestamp createTime;
}
