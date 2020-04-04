package com.star.date.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author star
 */
public class MyTest {
    public static void main(String[] args) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = dateFormat.parse("2019-01-01 07:05:31");

            LocalDateTime localDateTime = LocalDateTime.ofInstant(parse.toInstant(), ZoneId.systemDefault());
            System.out.println(localDateTime.toLocalDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
