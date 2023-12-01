import lombok.extern.slf4j.Slf4j;
import org.howard1209a.blog.BlogApplication;
import org.howard1209a.blog.mapper.BlogMapper;
import org.howard1209a.blog.mapper.LabelMapper;
import org.howard1209a.blog.pojo.Blog;
import org.howard1209a.blog.pojo.Label;
import org.howard1209a.blog.util.SnowflakeIdUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = BlogApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class T1 {
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    @Test
    public void t1() {
        String[] labels = new String[]{"色彩"};
        for (String label : labels) {
            labelMapper.insertOneLabel(new Label(snowflakeIdUtils.nextId(), label));
        }
    }

    @Test
    public void t2() { //where blog_id

    }
}
