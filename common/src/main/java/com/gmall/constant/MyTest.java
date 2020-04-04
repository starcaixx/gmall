package com.gmall.constant;

import com.alibaba.fastjson.JSON;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * @author star
 * @create 2019-04-02 16:36
 */
public class MyTest {
    public static void main(String[] args) {
//        val startUpLog: StartUpLog = JSON.parseObject(record.value(), classOf[StartUpLog])
//        val ts: Long = startUpLog.ts
//        val localDateTime: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault())
//        val date: LocalDate = localDateTime.toLocalDate
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime
        long ts = Instant.now().toEpochMilli();
//        Asia/Shanghai
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault());
//        System.out.println(System.currentTimeMillis()); System.currentTimeMillis()==Instant.now().toEpochMilli()
        System.out.println(localDateTime);
        System.out.println("=====================");
        LocalDate localDate = localDateTime.toLocalDate();
        System.out.println("=====================");
        System.out.println(localDate);
        System.out.println("=====================");
        LocalTime localTime = localDateTime.toLocalTime();
        System.out.println(localTime);
        System.out.println("=====================");
        System.out.println(localTime.getHour());
        System.out.println("=====================");
        System.out.println(localTime.getMinute());
        System.out.println("=====================");
        LocalDate now = LocalDate.now();
        System.out.println(now);
        /*Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
        for (String availableZoneId : availableZoneIds) {
            if (availableZoneId.startsWith("Asia")) {
                System.out.println(availableZoneId);
            }
        }*/

        System.out.println("===============");
        try {
            System.out.println(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
