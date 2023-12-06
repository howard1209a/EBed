import org.howard1209a.blog.BlogApplication;
import org.howard1209a.blog.constant.MqConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BlogApplication.class)
public class T3 {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void t1() {
        rabbitTemplate.convertAndSend(MqConstant.BLOG_EXCHANGE,MqConstant.BLOG_PUT_KEY,"hhh");
    }
}
