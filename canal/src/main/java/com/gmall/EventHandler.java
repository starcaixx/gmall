package com.gmall;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.google.common.base.CaseFormat;
import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.List;

/**
 * @author star
 */
public class EventHandler {
    public static void handleEvent(String tableName, CanalEntry.EventType eventType, List<CanalEntry.RowData> rowDataList) {
        if ("order_info".equals(tableName) && CanalEntry.EventType.INSERT == eventType) {
            for (CanalEntry.RowData rowData : rowDataList) {
                //一个RowData里包含了多个column，每个column包含了 name和 value
                List<CanalEntry.Column> columnsList = rowData.getAfterColumnsList();

                JSONObject jsonObject = new JSONObject();

                for (CanalEntry.Column column : columnsList) {
                    String columnName = column.getName();
                    String columnValue = column.getValue();

                    String propertiesName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
                    jsonObject.put(propertiesName,columnValue);
                }
                System.out.println("jsonObject:"+jsonObject);
                MyKafkaSender.send("superset",jsonObject.toString());
            }
        }
    }
}
