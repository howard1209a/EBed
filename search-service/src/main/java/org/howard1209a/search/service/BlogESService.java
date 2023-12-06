package org.howard1209a.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.howard1209a.search.pojo.BlogDoc;
import org.howard1209a.search.pojo.dto.BlogSearchDto;
import org.howard1209a.search.util.RedisLockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.howard1209a.search.constant.MqConstant.MYSQL_ES_SYNC_BLOG;

@Service
public class BlogESService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisLockUtil redisLockUtil;


    public List<BlogDoc> searchInfo(BlogSearchDto blogSearchDto) {
        // 查询blog表
        SearchRequest request = new SearchRequest("blog");

        // 复合查询，全部用must参与算分
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (blogSearchDto.getSearchContent() != null && !blogSearchDto.getSearchContent().equals("")) {
            boolQuery.must(QueryBuilders.multiMatchQuery(blogSearchDto.getSearchContent(), "userName", "title", "content"));
        }
        for (String label : blogSearchDto.getLabels()) {
            boolQuery.must(QueryBuilders.termsQuery("labels", label));
        }
        request.source().query(boolQuery);

        // 高亮显示，自动加<em>标签
        request.source().highlighter(new HighlightBuilder().field("content").field("title").field("userName").requireFieldMatch(false));

        // 排序方式，如果没有设置默认按相似度算分排序
        if ("收藏数排序".equals(blogSearchDto.getSortManner())) {
            request.source().sort("favouriteNum", SortOrder.DESC);
        } else if ("评论数排序".equals(blogSearchDto.getSortManner())) {
            request.source().sort("commentNum", SortOrder.DESC);
        } else if ("新发布".equals(blogSearchDto.getSortManner())) {
            request.source().sort("createTime", SortOrder.DESC);
        }

        try {
            // 发送查询
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<BlogDoc> handleResponse(SearchResponse response) {
        // 解析响应
        SearchHits searchHits = response.getHits();
        // 获取总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到" + total + "条数据");
        // 文档数组
        SearchHit[] hits = searchHits.getHits();
        // 遍历
        List<BlogDoc> blogDocs = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            BlogDoc blogDoc;
            try {
                blogDoc = objectMapper.readValue(json, BlogDoc.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            // 获取高亮结果
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null && highlightFields.size() != 0) {
                // 根据字段名获取高亮结果
                HighlightField highlightField = highlightFields.get("content");
                if (highlightField != null) {
                    // 获取高亮值
                    String content = highlightField.getFragments()[0].string();
                    // 覆盖非高亮结果
                    blogDoc.setContent(content);
                }
                highlightField = highlightFields.get("title");
                if (highlightField != null) {
                    String title = highlightField.getFragments()[0].string();
                    blogDoc.setTitle(title);
                }
                highlightField = highlightFields.get("userName");
                if (highlightField != null) {
                    String userName = highlightField.getFragments()[0].string();
                    blogDoc.setUserName(userName);
                }
            }
            blogDocs.add(blogDoc);
        }
        return blogDocs;
    }

    public List<String> searchPrefix(String prefix) {
        try {
            // 1.准备Request
            SearchRequest request = new SearchRequest("blog");
            // 2.准备DSL
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)
            ));
            // 3.发起请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 4.解析结果
            Suggest suggest = response.getSuggest();
            // 4.1.根据补全查询名称，获取补全结果
            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
            // 4.2.获取options
            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
            // 4.3.遍历
            List<String> list = new ArrayList<>(options.size());
            for (CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                list.add(text);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 新增和修改都是put操作
    public void putBlogDoc(String json) {
        Long blogId = null;
        try {
            blogId = objectMapper.readValue(json, BlogDoc.class).getId();
            IndexRequest request = new IndexRequest("blog").id(blogId.toString());
            request.source(json, XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // 双库同步完成，锁要解开
            redisLockUtil.unlock(MYSQL_ES_SYNC_BLOG + blogId);
        }
    }
}
