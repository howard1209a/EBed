package org.howard1209a.blog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserState {
    private String identifyImageNumber;
    private boolean isLogin;
    private Long userId;
    private List<Long> lastUploadedImgId;
}
