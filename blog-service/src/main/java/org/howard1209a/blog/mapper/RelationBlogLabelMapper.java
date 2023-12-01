package org.howard1209a.blog.mapper;

import org.apache.ibatis.annotations.Insert;
import org.howard1209a.blog.pojo.RelationBlogLabel;

public interface RelationBlogLabelMapper {
    @Insert("insert into relation_blog_label values(#{blogId},#{labelId})")
    public void insertRelation(RelationBlogLabel relationBlogLabel);
}
