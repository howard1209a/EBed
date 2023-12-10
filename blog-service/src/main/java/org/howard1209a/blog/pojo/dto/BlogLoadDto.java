package org.howard1209a.blog.pojo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String url;
    private boolean favorite;
    private List<String> labels;
}
