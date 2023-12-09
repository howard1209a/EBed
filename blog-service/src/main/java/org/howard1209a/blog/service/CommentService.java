package org.howard1209a.blog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.howard1209a.blog.constant.MqConstant;
import org.howard1209a.blog.feign.UserClient;
import org.howard1209a.blog.mapper.BlogMapper;
import org.howard1209a.blog.mapper.CommentMapper;
import org.howard1209a.blog.mapper.RelationUserCommentLikeMapper;
import org.howard1209a.blog.pojo.*;
import org.howard1209a.blog.pojo.doc.BlogDoc;
import org.howard1209a.blog.pojo.dto.CommentDto;
import org.howard1209a.blog.pojo.dto.Response;
import org.howard1209a.blog.util.MQUtil;
import org.howard1209a.blog.util.RedisLockUtil;
import org.howard1209a.blog.util.RedisUtil;
import org.howard1209a.blog.util.SnowflakeIdUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static org.howard1209a.blog.constant.BlogConstant.USER_COMMENT_LIKE_RELATION;
import static org.howard1209a.blog.constant.BlogConstant.USER_STATE_KEY;
import static org.howard1209a.blog.constant.MqConstant.MYSQL_ES_SYNC_BLOG;
import static org.howard1209a.blog.constant.UserConstant.EXP_COMMENT_LIKE_BY_OTHER;
import static org.howard1209a.blog.constant.UserConstant.EXP_LIMIT_LIKECOMMENT_LIMIT_HASHKEY;

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
    private BlogMapper blogMapper;
    @Autowired
    private RelationUserCommentLikeMapper relationUserCommentLikeMapper;
    @Autowired
    private UserClient userClient;
    @Autowired
    private BlogService blogService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MQUtil mqUtil;

    public void publishOneComment(String session, Comment comment) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        comment.setUserId(userState.getUserId());
        comment.setCommentId(snowflakeIdUtils.nextId());
        // 插入评论记录
        commentMapper.insertComment(comment);

        // 接下来更新博客记录(双库)，首先在博客条目上一把锁，es服务同步完这把锁才会解开
        redisLockUtil.blockingGetLock(MYSQL_ES_SYNC_BLOG + comment.getBlogId());
        blogMapper.plusCommentNum(comment.getBlogId());

        mqUtil.sendBlog(comment.getBlogId());
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

        userClient.addExp(userState.getUserId(), EXP_LIMIT_LIKECOMMENT_LIMIT_HASHKEY); // 尝试加一下自己的经验值
        Long userId = commentMapper.queryUserIdByCommentId(commentId);
        userClient.addExp(userId, EXP_COMMENT_LIKE_BY_OTHER); // 加被点赞人的经验值
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
