package org.howard1209a.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogDto {
    private String title;
    private Long imgId;
    private String content;
    private List<String> labels;
}
