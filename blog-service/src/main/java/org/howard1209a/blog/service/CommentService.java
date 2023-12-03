package org.howard1209a.blog.service;

import org.howard1209a.blog.feign.UserClient;
import org.howard1209a.blog.mapper.CommentMapper;
import org.howard1209a.blog.pojo.Comment;
import org.howard1209a.blog.pojo.User;
import org.howard1209a.blog.pojo.UserState;
import org.howard1209a.blog.pojo.dto.CommentDto;
import org.howard1209a.blog.pojo.dto.Response;
import org.howard1209a.blog.util.RedisUtil;
import org.howard1209a.blog.util.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Autowired
    private UserClient userClient;

    public void publishOneComment(String session, Comment comment) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        comment.setUserId(userState.getUserId());
        comment.setCommentId(snowflakeIdUtils.nextId());
        commentMapper.insertComment(comment);
    }

    public Response<List<CommentDto>> queryAllCommentForOneBlog(Long blogId) {
        List<CommentDto> commentDtos = new ArrayList<>();
        // 无父评论的评论
        List<Comment> comments = commentMapper.queryAllCommentForOneBlogWithNoFather(blogId);
        for(Comment comment:comments) {
            CommentDto commentDto = new CommentDto(comment);
            User user = userClient.queryInfoById(commentDto.getUserId());
            commentDto.setUserName(user.getUserName());
            commentDtos.add(commentDto);
        }
        // 有父评论的评论
        List<CommentDto> list = commentMapper.queryAllCommentForOneBlogWithFather(blogId);
        for(CommentDto commentDto:list) {
            User user = userClient.queryInfoById(commentDto.getUserId());
            User fatherUser = userClient.queryInfoById(commentDto.getFatherUserId());
            commentDto.setUserName(user.getUserName());
            commentDto.setFatherUserName(fatherUser.getUserName());
            commentDtos.add(commentDto);
        }
        return new Response<>(true, commentDtos);
    }
}
