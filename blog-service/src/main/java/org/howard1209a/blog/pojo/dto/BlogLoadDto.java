package org.howard1209a.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.howard1209a.blog.pojo.Blog;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogLoadDto {
    private Blog blog;
    private String userName;
    private String url;
    private List<String> labels;
}
