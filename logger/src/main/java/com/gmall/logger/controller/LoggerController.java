package com.gmall.logger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gmall.constant.GmallConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author star
 * @create 2019-04-01 18:34
 */
@RestController
public class LoggerController {

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(LoggerController.class);

    @PostMapping("log")
    public String doLog(@RequestParam("log") String log) {
        long ts = System.currentTimeMillis();

        JSONObject jsonObject = JSON.parseObject(log);
        jsonObject.put("ts",ts);
        System.out.println(log);
        if ("startup".equals(jsonObject.getString("type"))) {
            kafkaTemplate.send(GmallConstant.TOPIC_STARTUP,jsonObject.toJSONString());
        }else{
            kafkaTemplate.send(GmallConstant.TOPIC_EVENT,jsonObject.toJSONString());
        }
        logger.info(jsonObject.toJSONString());
        logger.info(log);
        return "success";
    }
}
