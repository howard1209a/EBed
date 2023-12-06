package org.howard1209a.search.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.howard1209a.search.constant.MqConstant;
import org.howard1209a.search.pojo.BlogDoc;
import org.howard1209a.search.service.BlogESService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlogListener {
    @Autowired
    private BlogESService blogESService;
    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = MqConstant.BLOG_PUT_QUEUE)
    public void listenBlogInsertOrUpdate(String message) {
        blogESService.putBlogDoc(message);
    }
}
