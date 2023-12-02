package org.howard1209a.blog.service;

import org.howard1209a.blog.mapper.CommentMapper;
import org.howard1209a.blog.pojo.Comment;
import org.howard1209a.blog.pojo.UserState;
import org.howard1209a.blog.pojo.dto.Response;
import org.howard1209a.blog.util.RedisUtil;
import org.howard1209a.blog.util.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.howard1209a.blog.constant.BlogConstant.USER_STATE_KEY;

@Service
public class CommentService {
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CommentMapper commentMapper;

    public void publishOneComment(String session, Comment comment) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        comment.setUserId(userState.getUserId());
        comment.setCommentId(snowflakeIdUtils.nextId());
        commentMapper.insertComment(comment);
    }

    public Response<List<Comment>> queryAllCommentForOneBlog(Long blogId) {
        List<Comment> comments = commentMapper.queryAllCommentForOneBlogWithNoFather(blogId);
        return new Response<>(true, comments);
    }
}
