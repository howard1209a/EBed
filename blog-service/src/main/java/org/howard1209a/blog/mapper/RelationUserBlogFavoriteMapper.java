package org.howard1209a.blog.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.howard1209a.blog.pojo.RelationUserBlogFavorite;

public interface RelationUserBlogFavoriteMapper {
    @Insert("insert into relation_user_blog_favorite values (#{userId},#{blogId})")
    public void insertOneFavorite(RelationUserBlogFavorite relationUserBlogFavorite);

    @Delete("delete from relation_user_blog_favorite where user_id = #{userId} and blog_id = #{blogId}")
    public int deleteOneFavorite(RelationUserBlogFavorite relationUserBlogFavorite);

    @Select("select * from relation_user_blog_favorite where user_id = #{userId} and blog_id = #{blogId}")
    public RelationUserBlogFavorite queryFavoriteByUserAndBlog(RelationUserBlogFavorite relationUserBlogFavorite);
}
