package org.howard1209a.blog.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.howard1209a.blog.pojo.Comment;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String userName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long commentId;
    private String commentContent;
    private Integer likesNum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createTime;
    private Integer floor;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fatherUserId;
    private String fatherUserName;
    private String fatherCommentContent;
    private boolean liked;

    public CommentDto(Comment comment) {
        commentId = comment.getCommentId();
        userId = comment.getUserId();
        commentContent = comment.getCommentContent();
        likesNum = comment.getLikesNum();
        createTime = comment.getCreateTime();
        floor = comment.getFloor();
    }
}
