package com.gmall.mock.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

/**
 * @author star
 * @create 2019-04-01 17:38
 */
public class LogUploader {

    public static void sendLogStream(String log) {
        try {
            URL url = new URL("http://hadoop60/log");
//            URL url = new URL("http://hadoop59:8080/log");
//            URL url = new URL("http://localhost:8080/log");

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("POST");

            Random random = new Random();

            conn.setRequestProperty("clientTime",(System.currentTimeMillis()+random.nextInt(25)*60*60*1000)+"");

            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            System.out.println("upload"+log);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(("log="+log).getBytes());

            outputStream.flush();
            outputStream.close();
            int code = conn.getResponseCode();
            System.out.println(code);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
