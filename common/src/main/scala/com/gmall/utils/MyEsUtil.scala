package com.gmall.utils

import com.gmall.constant.GmallConstant
import io.searchbox.client.config.HttpClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.core.{Bulk, BulkResult, Index}
;
/**
  * @author star
  * @create 2019-04-03 8:48
  */
object MyEsUtil {

    private var factory: JestClientFactory = null

    def getClient: JestClient = {
        if (factory == null) {
            build()
        }
        factory.getObject
    }

    def close(client: JestClient): Unit = {
        if (client != null) {
            client.shutdownClient()
        }
    }

    private def build(): Unit = {
//        multiThreaded 支持多线程
        factory = new JestClientFactory
        val esUrl = GmallConstant.ES_HOST + ":" + GmallConstant.ES_PORT
        factory.setHttpClientConfig(new HttpClientConfig.Builder(esUrl).multiThreaded(true)
          .maxTotalConnection(20).connTimeout(10000).readTimeout(10000).build())
    }

    def executeIndexBulk(indexName: String, list: List[Any]): Unit = {
        //index-database    type-table
        val builder: Bulk.Builder = new Bulk.Builder().defaultIndex(indexName).defaultType("_doc")

        //批处理
        for (doc <- list) {
            //doc-row
            val index: Index = new Index.Builder(doc).build()
            builder.addAction(index)
        }
        val client: JestClient = getClient

        val items: java.util.List[BulkResult#BulkResultItem] = client.execute(builder.build()).getItems
        print("save es = "+items.size())
        close(client)
    }

    def main(args: Array[String]): Unit = {
        val client: JestClient = getClient
        val source="{\n   \"mid\":\"mid_003\",\n   \"uid\":\"uid_004\"\n}"
        val index: Index = new Index.Builder(source).index("gmall_startup_log").`type`("_doc").build()
        client.execute(index)
        /*GET movie_index/_search
          {
              "query": {
                  "match_all": {}
              }
          }*/
        /*val client: JestClient = getClient
        val source = "{\n  \"query\": {\n    \"match_all\": {}\n  }\n}"
        //SearchSourceBuilder
        //QueryBuilder
        val search: Search = new Search.Builder(source).addIndex("gmall_dau").addType("movie").build()
        //Index 插入操作        index-database  type-table  document-row
        //Search 查询操作

        println(client.execute(search).getTotal)*/

        close(client)
    }
}
