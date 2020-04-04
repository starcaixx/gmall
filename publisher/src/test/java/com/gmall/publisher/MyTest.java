package com.gmall.publisher;

import com.alibaba.fastjson.JSON;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author star
 * @create 2019-04-04 11:37
 */
public class MyTest {

    public static void main(String[] args) {
//        jsonTest();
//        dateTest();
        long l = 100l;
        int a = (int) l;
        System.out.println(a);
    }

    private static void dateTest() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse("2018-04-04", dtf);
        LocalDate localDate1 = localDate.plusDays(-1);
        System.out.println(localDate1.toString());
    }

    private static void jsonTest() {
        ArrayList<Map<String, String>> list = new ArrayList<>();
        HashMap<String, String> m1 = new HashMap<>();
        m1.put("a", "aa");
        m1.put("b", "bb");
        m1.put("c", "cc");
        m1.put("d", "dd");
        m1.put("e", "ee");
        HashMap<String, String> m2 = new HashMap<>();
        m2.put("a", "aa");
        m2.put("b", "bb");
        m2.put("c", "cc");
        m2.put("d", "dd");
        m2.put("e", "ee");
        list.add(m1);
        list.add(m2);
        System.out.println(JSON.toJSONString(list));
    }
}
