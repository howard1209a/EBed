package org.howard1209a.blog.config;

import org.howard1209a.blog.constant.MqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(MqConstant.BLOG_EXCHANGE, true, false);
    }

    @Bean
    public Queue insertQueue() {
        return new Queue(MqConstant.BLOG_PUT_QUEUE, true);
    }

    @Bean
    public Binding putQueueBinding() {
        return BindingBuilder.bind(insertQueue()).to(directExchange()).with(MqConstant.BLOG_PUT_KEY);
    }
}
