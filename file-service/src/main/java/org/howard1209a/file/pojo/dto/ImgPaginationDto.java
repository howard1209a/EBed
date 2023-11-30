package org.howard1209a.file.pojo.dto;

import lombok.Data;

@Data
public class ImgPaginationDto {
    private Integer startIndex;
    private Integer length;
    String imgGroupName;
}
