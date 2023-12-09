package org.howard1209a.blog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.howard1209a.blog.constant.MqConstant;
import org.howard1209a.blog.feign.FileClient;
import org.howard1209a.blog.feign.UserClient;
import org.howard1209a.blog.mapper.BlogMapper;
import org.howard1209a.blog.mapper.CommentMapper;
import org.howard1209a.blog.mapper.LabelMapper;
import org.howard1209a.blog.mapper.RelationUserBlogFavoriteMapper;
import org.howard1209a.blog.pojo.*;
import org.howard1209a.blog.pojo.doc.BlogDoc;
import org.howard1209a.blog.pojo.dto.BlogDto;
import org.howard1209a.blog.pojo.dto.BlogLoadDto;
import org.howard1209a.blog.util.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.howard1209a.blog.constant.BlogConstant.*;
import static org.howard1209a.blog.constant.MqConstant.MYSQL_ES_SYNC_BLOG;
import static org.howard1209a.blog.constant.UserConstant.*;

@Service
public class BlogService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RelationUserBlogFavoriteMapper relationUserBlogFavoriteMapper;
    @Autowired
    private UserClient userClient;
    @Autowired
    private FileClient fileClient;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LabelService labelService;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;
    @Autowired
    private MQUtil mqUtil;

    public void publishOneBlog(BlogDto blogDto, String session) {
        Long blogId = snowflakeIdUtils.nextId();
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        Long userId = userState.getUserId();
        Blog blog = new Blog(blogId, userId, blogDto.getTitle(), blogDto.getImgId(), blogDto.getContent(), null, null, null);

        // 发布一篇博客，尝试加一下自己的经验值
        userClient.addExp(userId, EXP_LIMIT_PUBLISHBLOG_LIMIT_HASHKEY);

        // 接下来新增博客(双库)，首先在博客条目上一把锁，es服务同步完这把锁才会解开
        redisLockUtil.blockingGetLock(MYSQL_ES_SYNC_BLOG + blogId);

        blogMapper.insertOneBlog(blog); // 持久化blog
        labelService.saveRelation(blogDto.getLabels(), blogId); // 持久化label关系

        mqUtil.sendBlog(blogId);
    }

    public void browseRefresh(String session) {
        // 阅读博客尝试加一下经验值
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        userClient.addExp(userState.getUserId(), EXP_LIMIT_READBLOG_LIMIT_HASHKEY);

        redisUtil.setObject(BROWSED_BLOG_KEY + session, new BrowsedBlogList(new ArrayList<>()));
    }

    public List<BlogLoadDto> browseLoad(String session, Integer loadNum) {
        BrowsedBlogList browsedBlogList = redisUtil.getObject(BROWSED_BLOG_KEY + session, BrowsedBlogList.class);
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        List<Long> list = browsedBlogList.getList();
        List<Blog> blogs;
        if (list.size() == 0) {
            blogs = blogMapper.queryRandBlog(loadNum);
        } else {
            String browsedBlog = "";
            for (int i = 0; i < list.size(); i++) {
                if (i != list.size() - 1) {
                    browsedBlog += list.get(i) + ",";
                } else {
                    browsedBlog += list.get(i);
                }
            }
            blogs = blogMapper.queryRandBlogUnrepeatable(browsedBlog, loadNum);
        }
        List<BlogLoadDto> res = new ArrayList<>();
        for (Blog blog : blogs) {
            res.add(blog2BlogLoadDto(userState, blog));
        }

        redisLockUtil.blockingGetLock(BROWSED_BLOG_KEY + session);
        browsedBlogList = redisUtil.getObject(BROWSED_BLOG_KEY + session, BrowsedBlogList.class);
        list = browsedBlogList.getList();
        for (Blog blog : blogs) {
            list.add(blog.getBlogId());
        }
        redisUtil.setObject(BROWSED_BLOG_KEY + session, browsedBlogList);
        redisLockUtil.unlock(BROWSED_BLOG_KEY + session);
        return res;
    }

    public void favoriteBlog(String session, Long blogId) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);

        // 加一下博客所有者的经验值
        Blog blog = blogMapper.queryOneBlogById(blogId);
        userClient.addExp(blog.getUserId(), EXP_BLOG_FAVORITE_BY_OTHER);

        // 接下来更新博客记录(双库)，首先在博客条目上一把锁，es服务同步完这把锁才会解开
        redisLockUtil.blockingGetLock(MYSQL_ES_SYNC_BLOG + blogId);
        blogMapper.plusFavouriteNum(blogId);
        relationUserBlogFavoriteMapper.insertOneFavorite(new RelationUserBlogFavorite(userState.getUserId(), blogId));

        mqUtil.sendBlog(blogId);
    }

    public void unfavoriteBlog(String session, Long blogId) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);

        // 接下来更新博客记录(双库)，首先在博客条目上一把锁，es服务同步完这把锁才会解开
        redisLockUtil.blockingGetLock(MYSQL_ES_SYNC_BLOG + blogId);
        blogMapper.subtractFavouriteNum(blogId);
        relationUserBlogFavoriteMapper.deleteOneFavorite(new RelationUserBlogFavorite(userState.getUserId(), blogId));

        mqUtil.sendBlog(blogId);
    }

    public BlogLoadDto queryOneBlogById(String session, Long blogId) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        Blog blog = blogMapper.queryOneBlogById(blogId);
        return blog2BlogLoadDto(userState, blog);
    }

    private BlogLoadDto blog2BlogLoadDto(UserState userState, Blog blog) {
        User user = userClient.queryInfoById(blog.getUserId());
        Img img = fileClient.queryInfoById(blog.getImgId());
        String url = "http://localhost:10010/file/img/download/" + img.getImgId() + "." + img.getImgType();
        List<String> labels = labelMapper.queryLabelsForOneBlog(blog.getBlogId());
        boolean isFavorite = relationUserBlogFavoriteMapper.queryFavoriteByUserAndBlog(new RelationUserBlogFavorite(userState.getUserId(), blog.getBlogId())) != null;
        return new BlogLoadDto(blog, user.getUserName(), url, isFavorite, labels);
    }
}
