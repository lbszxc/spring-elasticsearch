package com.bsliao.esapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bsliao.esapi.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EsApiApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    // 测试索引创建
    @Test
    void testCreateIndex() throws IOException {
        // 1. 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("bsliao_index");
        // 2. 客户端执行创建请求 IndicesClient，请求后获得响应
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        // 3. 输出创建的索引
        System.out.println(response.toString());
    }


    // 测试获取索引，只能判断索引是否存在
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("bsliao_index");
        // 判断索引是否存在，true表示存在，false则不存在
        Boolean isFlag = client.indices().exists(request,RequestOptions.DEFAULT);
        System.out.println(isFlag);
    }

    // 测试删除已经存在的索引
    @Test
    void testDeleteIndex() throws IOException {
        // 1. 创建删除索引的请求
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("bsliao_index");
        // 2. 删除索引
        AcknowledgedResponse response = client.indices().delete(deleteIndexRequest,RequestOptions.DEFAULT);
        // 3. 打印输出结果是否删除成功
        System.out.println(response.isAcknowledged());
    }

    // 测试文档的创建
    @Test
    void testAddDocument() throws IOException, ParseException {
        // 1.创建实体对象
        User user = new User("bsliao",25,"男","1999-12-29");
        // 2.创建请求
        IndexRequest request = new IndexRequest("bsliao_index");
        // 3.创建规则put /bsliao/user/1
        request.id("1");
        // 超时设置
        request.timeout(TimeValue.timeValueSeconds(1)); // 设置1秒
        request.timeout("1s");  // 设置1秒

        // 4.将数据放入请求中
        request.source(JSON.toJSONString(user), XContentType.JSON);

        // 5.客户端发送请求
        IndexResponse response = client.index(request,RequestOptions.DEFAULT);

        // 6.打印输出
        System.out.println(response.toString());
        System.out.println(response.status());      // 响应状态
    }

    // 判断文档的内容是否存在 相当于GET /bsliao_index/user/1操作
    @Test
    void testExistDocument() throws IOException{
        // 1.创建获取文档的请求
        GetRequest request = new GetRequest("bsliao_index","1");
        // 2.不获取返回的 _source 的上下文了
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        GetResponse response = client.get(request,RequestOptions.DEFAULT);
        System.out.println(response.isExists());
    }

    // 测试获取文档的信息
    @Test
    void testGetDocument() throws IOException {
        // 1.创建获取文档的请求
        GetRequest request = new GetRequest("bsliao_index","1");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        // 2.打印_source中的内容
        System.out.println(response.getSourceAsString());
        // 3.返回的全部内容
        System.out.println(response);
    }

    // 测试更新文档信息  相当于PUT操作
    @Test
    void testUpdateDocument() throws IOException {
        // 1.创建更新文档的请求
        UpdateRequest request = new UpdateRequest("bsliao_index","1");
        request.timeout("1s");

        User user = new User("dt",20,"女","2003-10-20");
        // 2. doc更新文档内容
        request.doc(JSON.toJSONString(user),XContentType.JSON);

        UpdateResponse response = client.update(request,RequestOptions.DEFAULT);

        System.out.println(response.toString());
        System.out.println(response.status());
    }

    // 测试删除文档信息 相当于DELETE操作
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("bsliao_index","1");
        request.timeout("1s");  // 设置超时时间1秒
        DeleteResponse response = client.delete(request,RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    // 测试批量插入文档信息
    @Test
    void testBatchDocument() throws IOException {
        BulkRequest request = new BulkRequest();
        request.timeout("10s");

        List<User> userList = new ArrayList<>();
        userList.add(new User("张三",26,"男","1998-02-14"));
        userList.add(new User("李四",18,"男","2005-06-25"));
        userList.add(new User("李红",20,"女","2000-09-18"));
        userList.add(new User("王艳",21,"女","2002-08-08"));
        userList.add(new User("杨东",29,"男","1995-06-13"));

        // 批量处理请求
        for (int i = 0; i < userList.size(); i++) {
            request.add(
                    new IndexRequest("bsliao_index")
                            .id(""+i+1)
                            .source(JSONObject.toJSONString(userList.get(i)),XContentType.JSON)
            );

            // 批量更新和批量删除，就在这里修改对应的请求就可以了
            BulkResponse response = client.bulk(request,RequestOptions.DEFAULT);

            // 是否失败,返回false代表成功
            System.out.println(response.hasFailures());
        }

    }

    // 查询文档的信息
    @Test
    void testQueryDocument() throws IOException {

        SearchRequest request = new SearchRequest("bsliao_index");

        // 构建搜索的条件
        SearchSourceBuilder builder = new SearchSourceBuilder();

        // 查询条件，我们可以使用 QueryBuilders 工具来实现
        // QueryBuilders.termQuery  精确
        // QueryBuilders.matchAllQuery 匹配所有

        // 中文查询，查不出来这个样子QueryBuilders.termQuery("name.keyword", "xxxx");
        // 不是中文查询，QueryBuilders.termQuery("name", "xxxx")
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name.keyword","李四");
        // MatchAllQueryBuilder matchQueryBuilder = QueryBuilders.matchAllQuery();
        builder.query(termQueryBuilder);
        builder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        request.source(builder);

        // 查询
        SearchResponse response = client.search(request,RequestOptions.DEFAULT);

        // 打印输出
        System.out.println(JSON.toJSONString(response.getHits()));
        System.out.println("================================");
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }

    }


    @Test
    void test(){
        User user = new User("dt",23,"男","2001-12-23");
        System.out.println(user.toString());
    }

}
