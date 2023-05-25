package com.bsliao.escombat.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author bsliao
 * @Description 爬取数据的内容
 * @Date 2023/5/21 16:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
// 开启链式编程
@Accessors(chain = true)
public class Content {
    private String title;
    private String img;
    private String price;
}
