package com.bsliao.escombat.service.Impl;

import com.alibaba.fastjson.JSON;
import com.bsliao.escombat.pojo.Content;
import com.bsliao.escombat.service.ContentService;
import com.bsliao.escombat.util.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author bsliao
 * @Description service实现类
 * @Date 2023/5/21 17:27
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;


    // 1.解析数据放入es索引中
    @Override
    public Boolean parseContent(String keyword) throws IOException {
        List<Content> contentList = new HtmlParseUtil().parseJD(keyword);

        BulkRequest bulkRequest = new BulkRequest();

        bulkRequest.timeout(new TimeValue(2, TimeUnit.SECONDS));

        for (int i = 0; i < contentList.size(); i++) {
            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(contentList.get(i)), XContentType.JSON));
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        return !bulkResponse.isFragment();
    }

    // 分页查询 查询搜索的条件
    @Override
    public List<Map<String, Object>> findPage(String keyword, int pageNo, int pageSize) throws IOException {
        // 1.判断页数是否小于1
        if (pageSize <= 1){
            pageNo = 1;
        }

        // 2.查询请求
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        // 3.构建查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();

        // 4.分页
        builder.from(pageNo);
        builder.size(pageSize);

        // 5.精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title",keyword);
        builder.query(termQueryBuilder);
        builder.timeout(new TimeValue(60,TimeUnit.SECONDS));

        // 6.执行搜索
        searchRequest.source(builder);
        SearchResponse response = client.search(searchRequest,RequestOptions.DEFAULT);

        // 7.解析结果
        List<Map<String,Object>> resultList = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            resultList.add(hit.getSourceAsMap());
        }
        return resultList;
    }

    // 高亮显示分页查询
    @Override
    public List<Map<String, Object>> findHighlightPage(String keyword, int pageNo, int pageSize) throws IOException {
        // 1.判断页数是否小于1
        if (pageSize <= 1) {
            pageNo = 1;
        }
        // 2.创建搜索请求
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        // 3.构建查询搜索的条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // 4.分页
        builder.from(pageNo);
        builder.size(pageSize);

        // 5.精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title",keyword);
        builder.query(termQueryBuilder);
        builder.timeout(new TimeValue(60,TimeUnit.SECONDS));

        // 6.高亮显示关键字
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");    // 高亮显示的name
        highlightBuilder.requireFieldMatch(false);  // 关闭多个高亮显示
        highlightBuilder.preTags("<span style='color:red'>");   // 高亮显示的html格式【前缀】
        highlightBuilder.postTags("</span>");   // 高亮显示的html格式【后缀】
        builder.highlighter(highlightBuilder);

        // 7.执行搜索
        searchRequest.source(builder);
        SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);

        // 8.解析结果
        List<Map<String,Object>> mapList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, HighlightField> field =  hit.getHighlightFields();      // 高亮的数据
            HighlightField title = field.get("title");
            Map<String,Object> sourceMap =  hit.getSourceAsMap();       // 没有高亮的数据
            // 判断高亮的结果是否为null
            if (title != null){
                Text[] texts = title.fragments();
                String new_title = "";
                for (Text text : texts) {
                    new_title += text;          // 循环替换掉没有高亮的数据
                }
                sourceMap.put("title",new_title);
            }
            mapList.add(sourceMap);
        }
        return mapList;
    }
}
