package com.gmall.realtime

import java.time._

import com.alibaba.fastjson.JSON
import com.gmall.bean.StartUpLog
import com.gmall.constant.GmallConstant
import com.gmall.utils.{MyEsUtil, MyKafkaUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

import scala.collection.mutable.ListBuffer

/**
  * @author star 
  */
object RealtimeStartupAPP {
    def main(args: Array[String]): Unit = {
        val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RealtimeStartupAPP")

        val ssc = new StreamingContext(sparkConf, Seconds(5))

        val kafkaDStream: InputDStream[ConsumerRecord[String, String]] = MyKafkaUtil.getKafkaStream(GmallConstant.TOPIC_STARTUP, ssc)
        val filterDStream: DStream[StartUpLog] = kafkaDStream.map(record => {
//            record.value()
            val startUpLog: StartUpLog = JSON.parseObject(record.value(), classOf[StartUpLog])
            val ts: Long = startUpLog.ts
            val localDateTime: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault)
            startUpLog.logDate = localDateTime.toLocalDate.toString;
            val time: LocalTime = localDateTime.toLocalTime
            startUpLog.logHour = time.getHour.toString;
            startUpLog.logHourMinute = time.getHour + ":" + time.getMinute
            (startUpLog.mid, startUpLog)
        }).groupByKey().mapValues(_.take(1)).flatMap(_._2).transform(rdd => {
            println("before filter...." + rdd.count())
            val jedis = new Jedis("hadoop59", 6379)
            val date: LocalDate = LocalDate.now()
            val key = "dau:" + date.toString;
            val dauSet: java.util.Set[String] = jedis.smembers(key)
            println("dauSet:"+dauSet.size())
            jedis.close()
            val dauBC: Broadcast[java.util.Set[String]] = ssc.sparkContext.broadcast(dauSet)
            val filterRdd: RDD[StartUpLog] = rdd.filter(startlog => {
                !dauBC.value.contains(startlog.mid)
            })
            println("after filter...." + filterRdd.count())
            filterRdd
        })
        filterDStream.print()

        filterDStream.foreachRDD(rdd => {
            rdd.foreachPartition(its => {
                val list = new ListBuffer[StartUpLog]
                val jedis = new Jedis("hadoop59", 6379)

                for (elem <- its) {
                    val key = "dau:" + elem.logDate
                    jedis.sadd(key, elem.mid)
                    list += elem
                }
                MyEsUtil.executeIndexBulk(GmallConstant.ES_INDEX_DAU,list.toList)
                jedis.close()
            })
        })


        ssc.start()
        ssc.awaitTermination()
    }
}
