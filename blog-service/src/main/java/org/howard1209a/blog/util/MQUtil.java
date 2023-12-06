package org.howard1209a.blog.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.howard1209a.blog.constant.MqConstant;
import org.howard1209a.blog.feign.UserClient;
import org.howard1209a.blog.mapper.BlogMapper;
import org.howard1209a.blog.mapper.LabelMapper;
import org.howard1209a.blog.pojo.Blog;
import org.howard1209a.blog.pojo.User;
import org.howard1209a.blog.pojo.doc.BlogDoc;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class MQUtil {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserClient userClient;
    @Autowired
    private LabelMapper labelMapper;

    public void sendBlog(Long blogId) {
        Blog blog = blogMapper.queryOneBlogById(blogId); // 查出blog
        String message;
        try {
            BlogDoc blogDoc = blog2BlogDoc(blog);
            message = objectMapper.writeValueAsString(blogDoc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        rabbitTemplate.convertAndSend(MqConstant.BLOG_EXCHANGE, MqConstant.BLOG_PUT_KEY, message); // 生产一条消息到mq
    }

    private BlogDoc blog2BlogDoc(Blog blog) throws IOException {
        User user = userClient.queryInfoById(blog.getUserId());
        List<String> labels = labelMapper.queryLabelsForOneBlog(blog.getBlogId());
        BlogDoc blogDoc = new BlogDoc(blog, user.getUserName(), labels);

        // 利用ik分词器对博客title进行分词得到suggestion
        StringReader reader = new StringReader(blogDoc.getTitle());
        IKSegmenter segmenter = new IKSegmenter(reader, true);
        Lexeme lexeme;
        List<String> suggestion = new ArrayList<>();
        while ((lexeme = segmenter.next()) != null) {
            suggestion.add(lexeme.getLexemeText());
        }
        blogDoc.setSuggestion(suggestion);

        return blogDoc;
    }
}
