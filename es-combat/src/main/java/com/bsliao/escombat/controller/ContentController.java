package com.bsliao.escombat.controller;

import com.bsliao.escombat.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author bsliao
 * @Description 内容控制器
 * @Date 2023/5/23 15:53
 */
@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 将数据存入ES搜索引擎
     * @param keyword   存储数据的关键字
     * @return
     * @throws IOException
     */
    @GetMapping("/parse/{keyword}")
    public Boolean parseContent(@PathVariable("keyword") String keyword) throws IOException {
        return contentService.parseContent(keyword);
    }

    /**
     * 分页查询
     * @param keyword 搜索关键字
     * @param pageNo 页数
     * @param pageSize 一页显示的数量
     * @return
     * @throws IOException
     */
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> findPage(
            @PathVariable("keyword") String keyword,
            @PathVariable("pageNo") int pageNo,
            @PathVariable("pageSize") int pageSize) throws IOException {

        return contentService.findPage(keyword, pageNo, pageSize);

    }

    /**
     * 高亮显示分页查询
     * @param keyword 搜索关键字
     * @param pageNo 页数
     * @param pageSize 一页显示的数量
     * @return
     * @throws IOException
     */
    @GetMapping("/searchHight/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> findHighlightPage(
            @PathVariable("keyword") String keyword,
            @PathVariable("pageNo") int pageNo,
            @PathVariable("pageSize") int pageSize) throws IOException {

        return contentService.findHighlightPage(keyword, pageNo, pageSize);

    }

}
