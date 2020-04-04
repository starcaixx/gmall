package com.gmall.dict.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author star
 * @create 2019-04-15 10:48
 */
@RestController
public class DictController {

    @GetMapping("dict")
    public String dict() {
        StringBuilder sb = new StringBuilder();

        sb.append("蓝瘦香菇\n");
        sb.append("双卡双待\n");

//        response.addHeader("Last-Modified",new Date().toString());
        return sb.toString();

    }
}
