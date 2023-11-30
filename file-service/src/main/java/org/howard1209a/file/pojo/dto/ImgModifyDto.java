package org.howard1209a.file.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImgModifyDto {
    private String newName;
    private String newDescription;
    private String newGroupName;
    private Long imgId;
}
