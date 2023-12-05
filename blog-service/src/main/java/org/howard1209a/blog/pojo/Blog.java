package org.howard1209a.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blog {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long blogId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String title;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imgId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createTime;
    private Integer commentNum;
    private Integer favouriteNum;
}
