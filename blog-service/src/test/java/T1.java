import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.howard1209a.blog.BlogApplication;
import org.howard1209a.blog.feign.UserClient;
import org.howard1209a.blog.mapper.BlogMapper;
import org.howard1209a.blog.mapper.CommentMapper;
import org.howard1209a.blog.mapper.LabelMapper;
import org.howard1209a.blog.pojo.Blog;
import org.howard1209a.blog.pojo.Label;
import org.howard1209a.blog.pojo.User;
import org.howard1209a.blog.pojo.doc.BlogDoc;
import org.howard1209a.blog.util.SnowflakeIdUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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
    private CommentMapper commentMapper;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private UserClient userClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void t1() {
        String[] labels = new String[]{"色彩", "自然风景", "建筑", "人物"};
        for (String label : labels) {
            labelMapper.insertOneLabel(new Label(snowflakeIdUtils.nextId(), label));
        }
    }

    @Test
    public void t2() {
        List<String> list = labelMapper.queryLabelsForOneBlog(1180162266897723392l);
        System.out.println("");
    }

    @Test
    public void testBulkRequest() throws IOException {
        // 批量查询酒店数据
        List<Blog> blogs = blogMapper.queryAllBlog();

        // 1.创建Request
        BulkRequest request = new BulkRequest();
        // 2.准备参数，添加多个新增的Request
        for (Blog blog : blogs) {
            User user = userClient.queryInfoById(blog.getUserId());
            List<String> labels = labelMapper.queryLabelsForOneBlog(blog.getBlogId());
            // 2.1.转换为文档类型HotelDoc
            BlogDoc blogDoc = new BlogDoc(blog, user.getUserName(), labels);

            StringReader reader = new StringReader(blogDoc.getTitle());
            IKSegmenter segmenter = new IKSegmenter(reader, true);
            Lexeme lexeme;
            List<String> suggestion = new ArrayList<>();
            while ((lexeme = segmenter.next()) != null) {
                suggestion.add(lexeme.getLexemeText());
            }
            blogDoc.setSuggestion(suggestion);

            // 2.2.创建新增文档的Request对象
            String json = objectMapper.writeValueAsString(blogDoc);
            request.add(new IndexRequest("blog")
                    .id(blogDoc.getId().toString())
                    .source(json, XContentType.JSON));
        }
        // 3.发送请求
        client.bulk(request, RequestOptions.DEFAULT);
    }

    @Test
    public void testGetDocumentById() throws IOException {
        // 1.准备Request
        GetRequest request = new GetRequest("blog", "1180151103543717888");
        // 2.发送请求，得到响应
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        // 3.解析响应结果
        String json = response.getSourceAsString();
        BlogDoc blogDoc = objectMapper.readValue(json, BlogDoc.class);

        System.out.println(blogDoc);
    }
}
