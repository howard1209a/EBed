package org.howard1209a.blog.service;

import org.howard1209a.blog.feign.UserClient;
import org.howard1209a.blog.mapper.CommentMapper;
import org.howard1209a.blog.mapper.RelationUserCommentLikeMapper;
import org.howard1209a.blog.pojo.Comment;
import org.howard1209a.blog.pojo.RelationUserCommentLike;
import org.howard1209a.blog.pojo.User;
import org.howard1209a.blog.pojo.UserState;
import org.howard1209a.blog.pojo.dto.CommentDto;
import org.howard1209a.blog.pojo.dto.Response;
import org.howard1209a.blog.util.RedisLockUtil;
import org.howard1209a.blog.util.RedisUtil;
import org.howard1209a.blog.util.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.howard1209a.blog.constant.BlogConstant.USER_COMMENT_LIKE_RELATION;
import static org.howard1209a.blog.constant.BlogConstant.USER_STATE_KEY;

@Service
public class CommentService {
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RelationUserCommentLikeMapper relationUserCommentLikeMapper;
    @Autowired
    private UserClient userClient;

    public void publishOneComment(String session, Comment comment) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        comment.setUserId(userState.getUserId());
        comment.setCommentId(snowflakeIdUtils.nextId());
        commentMapper.insertComment(comment);
    }

    public Response<List<CommentDto>> queryAllCommentForOneBlog(String session, Long blogId) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        List<CommentDto> commentDtos = new ArrayList<>();
        // 无父评论的评论
        List<Comment> comments = commentMapper.queryAllCommentForOneBlogWithNoFather(blogId);
        for (Comment comment : comments) {
            CommentDto commentDto = new CommentDto(comment);
            User user = userClient.queryInfoById(commentDto.getUserId());
            commentDto.setUserName(user.getUserName());
            RelationUserCommentLike liked = relationUserCommentLikeMapper.queryOneLikeRelation(new RelationUserCommentLike(userState.getUserId(), comment.getCommentId()));
            commentDto.setLiked(liked != null);
            commentDtos.add(commentDto);
        }
        // 有父评论的评论
        List<CommentDto> list = commentMapper.queryAllCommentForOneBlogWithFather(blogId);
        for (CommentDto commentDto : list) {
            User user = userClient.queryInfoById(commentDto.getUserId());
            User fatherUser = userClient.queryInfoById(commentDto.getFatherUserId());
            commentDto.setUserName(user.getUserName());
            commentDto.setFatherUserName(fatherUser.getUserName());
            RelationUserCommentLike liked = relationUserCommentLikeMapper.queryOneLikeRelation(new RelationUserCommentLike(userState.getUserId(), commentDto.getCommentId()));
            commentDto.setLiked(liked != null);
            commentDtos.add(commentDto);
        }
        // 按照点赞数从高到低排序
        Collections.sort(commentDtos, new Comparator<CommentDto>() {
            @Override
            public int compare(CommentDto o1, CommentDto o2) {
                return o2.getLikesNum() > o1.getLikesNum() ? 1 : -1;
            }
        });
        return new Response<>(true, commentDtos);
    }

    public void likeOneComment(String session, Long commentId) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        String lockKey = USER_COMMENT_LIKE_RELATION + userState.getUserId() + ":" + commentId;
        redisLockUtil.blockingGetLock(lockKey);
        commentMapper.plusLikeForOneComment(commentId);
        relationUserCommentLikeMapper.insertOneLikeRelation(new RelationUserCommentLike(userState.getUserId(), commentId));
        redisLockUtil.unlock(lockKey);
    }

    public void unlikeOneComment(String session, Long commentId) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        String lockKey = USER_COMMENT_LIKE_RELATION + userState.getUserId() + ":" + commentId;
        redisLockUtil.blockingGetLock(lockKey);
        commentMapper.subtractLikeForOneComment(commentId);
        relationUserCommentLikeMapper.deleteOneLikeRelation(new RelationUserCommentLike(userState.getUserId(), commentId));
        redisLockUtil.unlock(lockKey);
    }
}
