package org.howard1209a.blog.pojo.doc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.howard1209a.blog.pojo.Blog;

import java.sql.Date;
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
    private List<String> suggestion;

    public BlogDoc(Blog blog, String userName, List<String> labels) {
        this.id = blog.getBlogId();
        this.userId = blog.getUserId();
        this.userName = userName;
        this.title = blog.getTitle();
        this.imgId = blog.getImgId();
        this.content = blog.getContent();
        this.createTime = blog.getCreateTime();
        this.labels = labels;
        this.commentNum = blog.getCommentNum();
        this.favouriteNum = blog.getFavouriteNum();
    }
}
