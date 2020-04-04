package com.gmall;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author star
 */
public class MyTest {
    public static void main(String[] args) {
        CanalConnector canalConnector = CanalConnectors.newSingleConnector(new InetSocketAddress("192.168.88.10", 11111), "example", "", "");
        while (true){
            canalConnector.connect();  //连接
            canalConnector.subscribe("test.test");  // 订阅表
            Message message = canalConnector.get(10);//抓取100条命令
            if(message.getEntries().size()==0){
                try {
//                    System.out.println("没有数据，休息5秒");
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
            }else{
                System.out.println("get data");
                System.out.println(message.getEntries());
            }

        }


    }
}
