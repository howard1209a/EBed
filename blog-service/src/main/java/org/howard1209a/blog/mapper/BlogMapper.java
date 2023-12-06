package org.howard1209a.blog.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.howard1209a.blog.pojo.Blog;

import java.util.List;

public interface BlogMapper {
    @Insert("insert into blog (blog_id,user_id,title,img_id,content) values (#{blogId},#{userId},#{title},#{imgId},#{content})")
    public void insertOneBlog(Blog blog);

    @Update("update blog set comment_num = comment_num + 1 where blog_id = #{blogId}")
    public void plusCommentNum(Long blogId);

    @Update("update blog set favourite_num = favourite_num + 1 where blog_id = #{blogId}")
    public void plusFavouriteNum(Long blogId);

    @Update("update blog set favourite_num = favourite_num - 1 where blog_id = #{blogId}")
    public void subtractFavouriteNum(Long blogId);

    @Select("select * from blog where blog_id not in (${browsedBlog}) order by rand() limit #{loadNum}")
    public List<Blog> queryRandBlogUnrepeatable(String browsedBlog, Integer loadNum);

    @Select("select * from blog order by rand() limit #{loadNum}")
    public List<Blog> queryRandBlog(Integer loadNum);

    @Select("select * from blog")
    public List<Blog> queryAllBlog();

    @Select("select * from blog where blog_id = #{blogId}")
    public Blog queryOneBlogById(Long blogId);
}
