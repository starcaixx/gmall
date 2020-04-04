package com.gmall;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author star
 */
public class CanalClient {
    public static void startConnect(){
        CanalConnector canalConnector = CanalConnectors.newSingleConnector(new InetSocketAddress("master", 11111), "example", "", "");

        while (true) {
            canalConnector.connect();
            canalConnector.subscribe("test.test");//订阅

            Message message = canalConnector.get(100);//抓取100条
            int size = message.getEntries().size();
            System.out.println("size:"+size);
            if (size == 0) {
                System.out.println(5);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                for (CanalEntry.Entry entry : message.getEntries()) {
                    if (entry.getEntryType() != CanalEntry.EntryType.ROWDATA) {
                        continue;
                    }
                    CanalEntry.RowChange rowChange = null;
                    try {
                        // entry经过反序列化得到的对象，包含了多行记录的变化值
                        rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                        ///一个rowchange里包含的数据变化集，其中每一个rowdata里面包含了一行的多个字段
                        EventHandler.handleEvent(entry.getHeader().getTableName(),rowChange.getEventType(),rowChange.getRowDatasList());
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
