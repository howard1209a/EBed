package org.howard1209a.blog.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.howard1209a.blog.pojo.RelationUserCommentLike;

public interface RelationUserCommentLikeMapper {
    @Select("select * from relation_user_comment_like where user_id = #{userId} and comment_id = #{commentId}")
    RelationUserCommentLike queryOneLikeRelation(RelationUserCommentLike relationUserCommentLike);

    @Delete("delete from relation_user_comment_like where user_id = #{userId} and comment_id = #{commentId}")
    Integer deleteOneLikeRelation(RelationUserCommentLike relationUserCommentLike);

    @Insert("insert into relation_user_comment_like values (#{userId},#{commentId})")
    void insertOneLikeRelation(RelationUserCommentLike relationUserCommentLike);
}
