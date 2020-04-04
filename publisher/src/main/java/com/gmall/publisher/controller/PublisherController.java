package com.gmall.publisher.controller;

import com.alibaba.fastjson.JSON;
import com.gmall.publisher.bean.Option;
import com.gmall.publisher.bean.Stat;
import com.gmall.publisher.service.PublisherService;
import com.star.gmall.publisher.bean.Option;
import com.star.gmall.publisher.bean.Stat;
import com.star.gmall.publisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author star
 * @create 2019-04-03 22:56
 */
@RestController
public class PublisherController {
    @Autowired
    PublisherService publisherService;

    @GetMapping("realtime-total")
    @RequestMapping(value = "realtime-total", method = RequestMethod.GET)
    public String realtimeHourDate(@RequestParam("date") String date) {
//        http://publisher:8070/realtime-total?date=2019-02-01
//        [{"id":"dau","name":"新增日活","value":1200},{"id":"new_mid","name":"新增用户","value":233}]
        //日活总数
        Long dauTotal = publisherService.getDauTotal(date);
        Map<String, String> dauMap = new HashMap<>();
        dauMap.put("id", "dau");
        dauMap.put("name", "新增日活");
        dauMap.put("value", dauTotal.toString());

        //新增用户
        Map<String, String> newMidMap = new HashMap<>();
        newMidMap.put("id", "dau");
        newMidMap.put("new_mid", "新增用户");
        newMidMap.put("value", "2000");

        ArrayList<Map<String, String>> arrayList = new ArrayList<>();
        arrayList.add(dauMap);
        arrayList.add(newMidMap);
        return JSON.toJSONString(arrayList);
//        return arrayList.toString();
    }
    //{"yesterday":{"钟点":数量},"today":{"钟点":数量}}
//    {
//        "yesterday":{"11":383,"12":123,"17":88,"19":200 },
//        "today":{"12":38,"13":1233,"17":123,"19":688 }
//    }
    //    @RequestMapping(value = "realtime-hour",method = RequestMethod.GET)

    @GetMapping("realtime-hour")
    public String getRealtimeHour(@RequestParam("id") String id, @RequestParam("date") String date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, dtf);
        LocalDate yesterDate = localDate.plusDays(-1);
        if ("dau".equals(id)) {
            //currentdate data
            Map cddauHours = publisherService.getDauHours(date);

            //yesterdate data
            Map yddayHourd = publisherService.getDauHours(yesterDate.toString());
            //combine
            Map dauMap = new HashMap();
            dauMap.put("today", cddauHours);
            dauMap.put("yesterday", yddayHourd);
            return JSON.toJSONString(dauMap);
        } else if ("order_amount".equals(id)) {
            //today
            Map todayOrderAmountHour = publisherService.getOrderAmountHour(date);
            //yesterday
            System.out.println("yesterDate....." + yesterDate);
            Map yesterdayOrderAmountHour = publisherService.getOrderAmountHour(yesterDate.toString());

            //combine
            Map orderAmountHourMap = new HashMap();
            orderAmountHourMap.put("today", todayOrderAmountHour);
            orderAmountHourMap.put("yesterday", yesterdayOrderAmountHour);

            return JSON.toJSONString(orderAmountHourMap);
        }

        return null;
    }
//            http://publisher:8070/realtime-hour?id=dau&&date=2019-02-01

    @GetMapping("sale_detail")
    public String getSaleDetail(@RequestParam("date")String date,@RequestParam("startPage") int startPage,@RequestParam("size") int size, @RequestParam("keyword") String keyword) {
        //根据条件查询
        Map saleDetail = publisherService.getSaleDetail(date, keyword, startPage, size, "user_gender", 2);

        //total
        int total = (int) saleDetail.get("total");
        Map genderAggsMap = (Map) saleDetail.get("aggs");

        Long maleCount = (Long) genderAggsMap.get("M");
        Long femaleCount = (Long) genderAggsMap.get("F");
        //占比
        double maleRate = Math.round(maleCount * 1000D / total) / 10D;
        double femaleRate = Math.round(femaleCount * 1000D / total) / 10D;

        List<Option> genderOptions = new ArrayList<>();
        genderOptions.add(new Option("male",maleRate));
        genderOptions.add(new Option("female",femaleRate));

        Stat stat = new Stat();
        stat.setOptions(genderOptions);
        stat.setTitle("user proportion");

        Map ageMap = publisherService.getSaleDetail(date, keyword, startPage, size, "user_age", 100);
        Map ageAggsMap = (Map) ageMap.get("aggs");

        int age_20 = 0;
        int age_20_30 = 0;
        int age_30 = 0;

        for (Object obj : ageAggsMap.entrySet()) {
            Map.Entry entry = (Map.Entry) obj;
            String aggStr = (String) entry.getKey();
            Integer age = Integer.valueOf(aggStr);
            Long count = (Long) entry.getValue();

            if (age < 20) {
                age_20 += count;
            } else if (age >= 20 && age <= 30) {
                age_20_30+=count;
            } else {
                age_30+= count;
            }
        }

        //每个年龄占比
        double age_20_rate = Math.round(age_20 * 1000D / total) / 10D;
        double age_20_30_rate = Math.round(age_20_30 * 1000D / total) / 10D;
        double age_30_rate = Math.round(age_30 * 1000D / total) / 10D;

        List<Option> aggeOptions = new ArrayList<>();
        aggeOptions.add(new Option("20 years old down",age_20_rate));
        aggeOptions.add(new Option("20~30 years old",age_20_30_rate));
        aggeOptions.add(new Option("30 years old up",age_30_rate));

        Stat ageStat = new Stat();

        ageStat.setOptions(aggeOptions);
        ageStat.setTitle("age proportion");

        List<Stat> statList = new ArrayList<>();
        statList.add(stat);
        statList.add(ageStat);

        //return result
        Map saleMap = new HashMap();
        saleMap.put("total",total);
        saleMap.put("stat",statList);
        saleMap.put("detail",saleDetail.get("ditail"));

        return JSON.toJSONString(saleDetail);
    }
}
