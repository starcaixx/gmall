package com.gmall.dw2es

import com.gmall.bean.SaleDetailDaycount
import com.gmall.constant.GmallConstant
import com.gmall.utils.MyEsUtil
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.collection.mutable.ListBuffer


/**
  * @author star 
  */
object DW2Esearch {

    def main(args: Array[String]): Unit = {
        var date = "";
        val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("DW2Esearch")

        val session: SparkSession = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()

        session.sql("use gmall")

        val saleDetailDayCounts: RDD[SaleDetailDaycount] = session.sql("select   user_id,sku_id,user_gender,cast(user_age as int) user_age,user_level,cast(sku_price as double),sku_name,sku_tm_id, sku_category3_id,sku_category2_id,sku_category1_id,sku_category3_name,sku_category2_name,sku_category1_name,spu_id,sku_num,cast(order_count as bigint) order_count,cast(order_amount as double) order_amount,dt from dws_sale_detail_daycount where dt='" + date + "'").as[SaleDetailDaycount].rdd

        saleDetailDayCounts.foreachPartition(saleDetails => {
            val listBuffer = new ListBuffer[SaleDetailDaycount]
            for (elem <- saleDetails) {
                listBuffer += elem
                if (listBuffer.size == 100) {
                    MyEsUtil.executeIndexBulk(GmallConstant.ES_INDEX_SALE_DETAIL, listBuffer.toList)
                }
            }
            MyEsUtil.executeIndexBulk(GmallConstant.ES_INDEX_SALE_DETAIL, listBuffer.toList)
        })
    }

}
