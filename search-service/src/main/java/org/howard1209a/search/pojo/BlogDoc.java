package org.howard1209a.search.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogDoc {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String userName;
    private String title;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgId;
    private String content;
    private Timestamp createTime;
    private List<String> labels;
    private Integer commentNum;
    private Integer favouriteNum;
    private List<String> suggestion;
}
