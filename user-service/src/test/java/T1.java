import lombok.extern.slf4j.Slf4j;
import org.howard1209a.user.UserApplication;
import org.howard1209a.user.feign.FileClient;
import org.howard1209a.user.mapper.RegisterMapper;
import org.howard1209a.user.mapper.RelationUserUserFollowMapper;
import org.howard1209a.user.pojo.User;
import org.howard1209a.user.pojo.dto.LoginDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = UserApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class T1 {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private FileClient fileClient;
    @Autowired
    private RelationUserUserFollowMapper relationUserUserFollowMapper;

    @Test
    public void t1() {
        relationUserUserFollowMapper.insertOneFollowRelation(1183048038168539136l,1183048038168539136l);
        log.info(relationUserUserFollowMapper.deleteOneFollowRelation(1183048038168539136l,1183048038168539136l)+"");
    }
}
