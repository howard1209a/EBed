package org.howard1209a.blog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationBlogLabel {
    private Long blogId;
    private Long labelId;
}
