package org.howard1209a.user.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpDto {
    private Integer exp;
    private Integer level;
    private Integer upperBound;

    public ExpDto(Integer exp) {
        this.exp = exp;
        if (exp >= 0 && exp < 200) {
            this.upperBound = 200;
            this.level = 1;
        } else if (exp >= 200 && exp < 1500) {
            this.upperBound = 1500;
            this.level = 2;
        } else if (exp >= 1500 && exp < 4500) {
            this.upperBound = 4500;
            this.level = 3;
        } else if (exp >= 4500 && exp < 10800) {
            this.upperBound = 10800;
            this.level = 4;
        } else if (exp >= 10800 && exp < 28800) {
            this.upperBound = 28800;
            this.level = 5;
        } else {
            this.upperBound = 28800;
            this.exp = 28800;
            this.level = 6;
        }
    }
}
