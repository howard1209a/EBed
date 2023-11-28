package org.howard1209a.file.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImgGroup {
    private long imgGroupId;
    private String imgGroupName;
    private long userId;
}
