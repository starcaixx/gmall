package com.gmall.bean

/**
  * @author star 
  * @create 2019-04-02 15:34 
  */
case class StartUpLog(mid:String,uid:String,appid:String,area:String,os:String,ch:String,
                      logType:String,vs:String,var logDate:String,var logHour:String,var logHourMinute:String,var ts:Long)


case class OrderInfo(
                      area: String,
                      consignee: String,
                      orderComment: String,
                      consigneeTel: String,
                      operateTime: String,
                      orderStatus: String,
                      paymentWay: String,
                      userId: String,
                      imgUrl: String,
                      totalAmount: Double,
                      expireTime: String,
                      deliveryAddress: String,
                      createTime: String,
                      trackingNo: String,
                      parentOrderId: String,
                      outTradeNo: String,
                      id: String,
                      tradeBody: String,
                      var createDate: String,
                      var createHour: String,
                      var createHourMinute: String
                    )