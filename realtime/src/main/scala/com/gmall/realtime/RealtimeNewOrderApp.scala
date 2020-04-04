package com.gmall.realtime

import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneId}
import java.util.Date

import com.alibaba.fastjson.JSON
import com.gmall.bean.OrderInfo
import com.gmall.constant.GmallConstant
import com.gmall.utils.{MyEsUtil, MyKafkaUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * @author star 
  * @create 2019-04-09 18:46 
  */
object RealtimeNewOrderApp {

    def main(args: Array[String]): Unit = {
        val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RealtimeNewOrderApp")

        val ssc = new StreamingContext(sparkConf, Seconds(5))

        val kafkaDStream: InputDStream[ConsumerRecord[String, String]] = MyKafkaUtil.getKafkaStream(GmallConstant.KAFKA_TOPIC_NEW_ORDER, ssc)

        kafkaDStream.map(record => {
            val str: String = record.value()
            val orderInfo: OrderInfo = JSON.parseObject(str, classOf[OrderInfo])

            val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val parse: Date = dateFormat.parse(orderInfo.createTime)
            val localDateTime: LocalDateTime = LocalDateTime.ofInstant(parse.toInstant, ZoneId.systemDefault)

            orderInfo.createDate = localDateTime.toLocalDate.toString
            orderInfo.createHour = localDateTime.getHour.toString
            orderInfo.createHourMinute = localDateTime.getHour + "" + localDateTime.getMinute
            orderInfo
        }).foreachRDD(rdd=>{
            rdd.foreachPartition(orders=>{
                MyEsUtil.executeIndexBulk(GmallConstant.ES_INDEX_NEW_ORDER,orders.toList)
            })
        })


        ssc.start()
        ssc.awaitTermination()
    }

}
