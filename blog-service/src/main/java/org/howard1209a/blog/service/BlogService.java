package org.howard1209a.blog.service;

import org.howard1209a.blog.feign.FileClient;
import org.howard1209a.blog.feign.UserClient;
import org.howard1209a.blog.mapper.BlogMapper;
import org.howard1209a.blog.mapper.CommentMapper;
import org.howard1209a.blog.mapper.LabelMapper;
import org.howard1209a.blog.pojo.*;
import org.howard1209a.blog.pojo.dto.BlogDto;
import org.howard1209a.blog.pojo.dto.BlogLoadDto;
import org.howard1209a.blog.util.RedisLockUtil;
import org.howard1209a.blog.util.RedisUtil;
import org.howard1209a.blog.util.SnowflakeIdUtils;
import org.howard1209a.blog.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.howard1209a.blog.constant.BlogConstant.BROWSED_BLOG_KEY;
import static org.howard1209a.blog.constant.BlogConstant.USER_STATE_KEY;

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
    private UserClient userClient;
    @Autowired
    private FileClient fileClient;
    @Autowired
    private RedisLockUtil redisLockUtil;

    public void publishOneBlog(BlogDto blogDto, Long blogId, String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        Long userId = userState.getUserId();
        Blog blog = new Blog(blogId, userId, blogDto.getTitle(), blogDto.getImgId(), blogDto.getContent(), null);
        blogMapper.insertOneBlog(blog);
    }

    public void browseRefresh(String session) {
        redisUtil.setObject(BROWSED_BLOG_KEY + session, new BrowsedBlogList(new ArrayList<>()));
    }

    public List<BlogLoadDto> browseLoad(String session, Integer loadNum) {
        BrowsedBlogList browsedBlogList = redisUtil.getObject(BROWSED_BLOG_KEY + session, BrowsedBlogList.class);
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
            User user = userClient.queryInfoById(blog.getUserId());
            Img img = fileClient.queryInfoById(blog.getImgId());
            String url = "http://localhost:10010/file/img/download/" + img.getImgId() + "." + img.getImgType();
            List<String> labels = labelMapper.queryLabelsForOneBlog(blog.getBlogId());
            res.add(new BlogLoadDto(blog, user.getUserName(), url, labels));
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
}
