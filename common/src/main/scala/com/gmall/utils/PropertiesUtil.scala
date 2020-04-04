package com.gmall.utils

import java.util.ResourceBundle
; /**
  * @author star
  * @create 2019-04-02 11:33
  */
object PropertiesUtil {
    private val bundle:ResourceBundle  = ResourceBundle.getBundle("config")
    def main(args: Array[String]): Unit = {
        val properties: String = PropertiesUtil.load("kafka.broker.list")

        println("properties:"+properties)

    }

    def load(propertiesName:String): String ={
        bundle.getString(propertiesName)
    }
}
