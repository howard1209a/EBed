import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.howard1209a.file.FileApplication;
import org.howard1209a.file.mapper.ImgGroupMapper;
import org.howard1209a.file.mapper.ImgMapper;
import org.howard1209a.file.pojo.Img;
import org.howard1209a.file.pojo.ImgGroup;
import org.howard1209a.file.pojo.UserState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = FileApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class T1 {
    @Autowired
    private ImgGroupMapper imgGroupMapper;
    @Autowired
    private ImgMapper imgMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void t1() throws JsonProcessingException {
        System.out.println(imgMapper.countImgForGroup(1179410526581698560l,"默认分组"));
    }
}
