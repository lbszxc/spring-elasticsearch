package com.bsliao.esapi.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 1、找对象
// 2、放到spring中待用
// 3、如果是springboot 就先分析源码
/**
 * @Author bsliao
 * @Description Es配置文件
 * @Date 2023/5/16 11:28
 */
@Configuration
public class EsClientConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient restHighLevelClient =
                new RestHighLevelClient(RestClient.builder(new HttpHost("localhost",9200,"http")));
        return restHighLevelClient;
    }

}
