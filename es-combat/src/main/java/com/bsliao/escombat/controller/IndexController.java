package com.bsliao.escombat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author bsliao
 * @Description 跳转页面
 * @Date 2023/5/21 16:39
 */
@Controller
public class IndexController {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

}
