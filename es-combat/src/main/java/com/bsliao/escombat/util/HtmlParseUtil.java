package com.bsliao.escombat.util;

import com.bsliao.escombat.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author bsliao
 * @Description 爬取京东数据工具类
 * @Date 2023/5/21 16:51
 */
public class HtmlParseUtil {

    public List<Content> parseJD(String keyword) throws IOException {
        List<Content> contentList = new ArrayList<>();

        // 获得请求 https://search.jd.com/Search?keyword=java
        // 前提：需要联网，不能获取ajax数据
        String url = "https://search.jd.com/Search?keyword="+keyword;

        // 解析网页 (jsoup返回Document就是浏览器的Document对象）
        Document document = Jsoup.parse(new URL(url),30000);
        // 所有在js中可以使用的方法，这里都能用
        Element element = document.getElementById("J_goodsList");
        // 获取所有的li标签
        Elements elements = element.getElementsByTag("li");
        // 循环获取li标签中数据
        for (Element item : elements) {
            // 关于图片特别多的网站，所有的图片都是延迟加载的 source-data-lazy-img
            String  img = item.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String title = item.getElementsByClass("p-name").eq(0).text();
            String prices = item.getElementsByClass("p-price").eq(0).text();

            Content content = new Content();
            content.setTitle(title).setImg(img).setPrice(prices);

            contentList.add(content);
        }

        return contentList;

    }

}
