package org.howard1209a.search.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogDoc {
    private Long id;
    private Long userId;
    private String userName;
    private String title;
    private Long imgId;
    private String content;
    private Timestamp createTime;
    private List<String> labels;
    private Integer commentNum;
    private Integer favouriteNum;
}
