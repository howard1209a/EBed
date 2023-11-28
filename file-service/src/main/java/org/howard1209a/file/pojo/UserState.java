package org.howard1209a.file.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserState {
    private String identifyImageNumber;
    private boolean isLogin;
    private Long userId;
}
