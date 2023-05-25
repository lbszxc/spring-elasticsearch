package com.bsliao.escombat.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author bsliao
 * @Description sevice接口
 * @Date 2023/5/21 17:25
 */
public interface ContentService {

    // 判断数据是否上传到ES库中
    public Boolean parseContent(String keyword) throws IOException;

    // 分页查询
    public List<Map<String,Object>> findPage(String keyword,int pageNo,int pageSize) throws IOException;

    // 高亮分页查询
    public List<Map<String, Object>> findHighlightPage(String keyword,int pageNo,int pageSize) throws IOException;

}
