package org.howard1209a.blog.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.howard1209a.blog.pojo.Comment;
import org.howard1209a.blog.pojo.dto.CommentDto;

import java.util.List;

public interface CommentMapper {
    @Insert("INSERT INTO comment (comment_id, user_id, blog_id, comment_content, father_comment_id, floor)\n" +
            "SELECT \n" +
            "    #{commentId},\n" +
            "    #{userId},\n" +
            "    #{blogId},\n" +
            "    #{commentContent},\n" +
            "    #{fatherCommentId},\n" +
            "    (SELECT COUNT(*) + 1 FROM comment WHERE blog_id = #{blogId})\n" +
            "FROM dual;\n")
    void insertComment(Comment comment);

    @Select("select * from comment where blog_id = #{blogId} and father_comment_id is null")
    List<Comment> queryAllCommentForOneBlogWithNoFather(Long blogId);

    @Select("select c1.user_id as userId,c1.comment_id as commentId,c1.comment_content as commentContent,c1.likes_num as likesNum,c1.create_time as createTime,c1.floor as floor,c2.user_id as fatherUserId,c2.comment_content as fatherCommentContent from comment as c1,comment as c2 where c1.blog_id = #{blogId} and c1.father_comment_id is not null and c1.father_comment_id = c2.comment_id")
    List<CommentDto> queryAllCommentForOneBlogWithFather(Long blogId);

    @Update("update comment set likes_num = likes_num + 1 where comment_id = #{commentId}")
    void plusLikeForOneComment(Long commentId);

    @Update("update comment set likes_num = likes_num - 1 where comment_id = #{commentId}")
    void subtractLikeForOneComment(Long commentId);

    @Select("select user_id from comment where comment_id = #{commentId}")
    Long queryUserIdByCommentId(Long commentId);
}
