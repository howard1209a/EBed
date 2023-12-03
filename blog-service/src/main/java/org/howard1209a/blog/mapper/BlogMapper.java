package org.howard1209a.blog.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.howard1209a.blog.pojo.Blog;

import java.util.List;

public interface BlogMapper {
    @Insert("insert into blog (blog_id,user_id,title,img_id,content) values (#{blogId},#{userId},#{title},#{imgId},#{content})")
    public void insertOneBlog(Blog blog);

    @Select("select * from blog where blog_id not in (${browsedBlog}) order by rand() limit #{loadNum}")
    public List<Blog> queryRandBlogUnrepeatable(String browsedBlog, Integer loadNum);

    @Select("select * from blog order by rand() limit #{loadNum}")
    public List<Blog> queryRandBlog(Integer loadNum);

    @Select("select * from blog")
    public List<Blog> queryAllBlog();
}
