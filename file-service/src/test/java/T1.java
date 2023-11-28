import lombok.extern.slf4j.Slf4j;
import org.howard1209a.file.FileApplication;
import org.howard1209a.file.mapper.ImgGroupMapper;
import org.howard1209a.file.pojo.Img;
import org.howard1209a.file.pojo.ImgGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = FileApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class T1 {
    @Autowired
    private ImgGroupMapper imgGroupMapper;
    @Test
    public void t1() {
        ImgGroup group = new ImgGroup(2, "nuibn", 1178833045436903424l);
        imgGroupMapper.saveGroup(group);
        System.out.println(imgGroupMapper.checkGroupNameForOneUser(group));
    }
}
