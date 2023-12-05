package org.howard1209a.blog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationUserBlogFavorite {
    private Long userId;
    private Long blogId;
}
