package com.gmall.publisher.service.impl;

import com.gmall.constant.GmallConstant;
import com.gmall.publisher.service.PublisherService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author star
 * @create 2019-04-03 22:55
 */
@Service
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    JestClient client;

    @Override
    public Long getDauTotal(String date) {
//        [{"id":"dau","name":"新增日活","value":1200},{"id":"new_mid","name":"新增用户","value":233}]
        long dauTotal = 0;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("logDate", date);
        queryBuilder.filter(matchQueryBuilder);
        searchSourceBuilder.query(queryBuilder);
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(GmallConstant.ES_INDEX_DAU).addType("_doc").build();

        try {
            SearchResult searchResult = client.execute(search);
            dauTotal = searchResult.getTotal();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dauTotal;
    }

    @Override
    public Long getNewMidTotal(String date) {
        return null;
    }

    @Override
    public Map getDauHours(String date) {

        /*GET gmall_dau/_search
{
  "query": {
    "bool": {
      "filter": {
        "match":{
          "logDate":"2019-04-03"
        }
      }
    }
  },
  "aggs": {
    "groupby_hour": {
      "terms": {
        "field": "logHour.keyword",
        "size": 10
      }
    }
  }
}*/
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("logDate", date);
        boolQueryBuilder.filter(matchQueryBuilder);
        TermsBuilder aggregationBuilder = AggregationBuilders.terms("groupby_hour").field("logHour.keyword");
        sourceBuilder.query(boolQueryBuilder).aggregation(aggregationBuilder);

        Search search = new Search.Builder(sourceBuilder.toString()).addIndex(GmallConstant.ES_INDEX_DAU).addType("_doc").build();

/*
"aggregations" : {
    "groupby_hour" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "19",
          "doc_count" : 57
        },
        {
          "key" : "20",
          "doc_count" : 51
        }
      ]
    }
  }
*/
        HashMap<String,Long> dauMap =  new HashMap<>();
        try {
            SearchResult searchResult = client.execute(search);
            TermsAggregation groupby_hour = searchResult.getAggregations().getTermsAggregation("groupby_hour");
            List<TermsAggregation.Entry> buckets = groupby_hour.getBuckets();

            for (TermsAggregation.Entry bucket : buckets) {
                dauMap.put(bucket.getKey(),bucket.getCount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dauMap;
    }

    @Override
    public Double getOrderAmount(String date) {
        /*GET gmall_new_order/_search
        {
            "query": {
            "bool": {
                "filter": {
                    "term": {
                        "createDate": "2019-01-01"
                    }
                }
            }
        },
            "aggs": {
            "sum_totalamount": {
                "sum": {
                    "field": "totalAmount"
                }
            }
        }
        }*/
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        SumBuilder aggres = AggregationBuilders.sum("sum_totalamount").field("totalAmount");

        sourceBuilder.query(new BoolQueryBuilder().filter(new TermQueryBuilder("createDate",date))).aggregation(aggres);

        Search search = new Search.Builder(sourceBuilder.toString()).addIndex(GmallConstant.ES_INDEX_NEW_ORDER).addType(GmallConstant.ES_DEFAULT_TYPE).build();
        double sum_totalamount = 0l;
        try {
            SearchResult result = client.execute(search);
            sum_totalamount = result.getAggregations().getSumAggregation("sum_totalamount").getSum();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sum_totalamount;
    }

    @Override
    public Map getOrderAmountHour(String date) {
        /*
        GET gmall_new_order/_search
{
  "query": {
    "bool": {
      "filter": {
        "term": {
          "createDate": "2019-01-01"
        }
      }
    }
  },
  "aggs": {
    "groupby_createHour": {
      "terms": {
        "field": "createHour",
        "size": 10
      },
      "aggs": {
        "sum_totalamount": {
          "sum": {
            "field": "totalAmount"
          }
        }
      }
    }
  }
}
         */

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder filter = new BoolQueryBuilder().filter(new TermsQueryBuilder("createDate",date));
        TermsBuilder termsBuilder = AggregationBuilders.terms("groupby_createHour").field("createHour").size(24);
        SumBuilder sumBuilder = AggregationBuilders.sum("sumTotalAmount").field("totalAmount");

        termsBuilder.subAggregation(sumBuilder);//注意这里是包含关系
        sourceBuilder.query(filter).aggregation(termsBuilder);

        Search search = new Search.Builder(sourceBuilder.toString()).addIndex(GmallConstant.ES_INDEX_NEW_ORDER).addType(GmallConstant.ES_DEFAULT_TYPE).build();

        Map orderAmountMap= new HashMap();;

        try {
            SearchResult result = client.execute(search);
            System.out.println("total:"+result.getTotal());
            if (result.getTotal() == 0) {
                return null;
            }
            List<TermsAggregation.Entry> buckets = result.getAggregations().getTermsAggregation("groupby_createHour").getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                String hour = bucket.getKey();

                Double sum_totalamount = bucket.getSumAggregation("sumTotalAmount").getSum();

                orderAmountMap.put(hour,sum_totalamount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orderAmountMap;
    }

//    http://localhost:8070/sale_detail?date=2019-04-01&&startpage=1&&size=5&&keyword=手机小米
    @Override
    public Map getSaleDetail(String date, String keyword, int startPage, int size, String aggFieldName, int aggsSize) {
        /*
        GET gmall_sale_detail/_search
{
  "query": {
    "bool": {
      "filter": {
        "term": {
          "dt": "2019-02-14"
        }
      },
      "must": [
        {"match":{
          "sku_name": {
            "query": "小米手机",
            "operator": "and"
          }
         }

        }
     ]
    }
  }
  , "aggs":  {
    "groupby_age": {
      "terms": {
        "field": "user_age"
      }
    }
  }
  ,
  "size": 2
  , "from": 0
}
         */
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(new BoolQueryBuilder().filter(new TermsQueryBuilder("dt",date)).must(new MatchQueryBuilder("sku_name",keyword).operator(MatchQueryBuilder.Operator.AND)));
        TermsBuilder termsBuilder = AggregationBuilders.terms("groupby_" + aggFieldName).field(aggFieldName).size(aggsSize);
        sourceBuilder.aggregation(termsBuilder);

        sourceBuilder.from((startPage-1)*size).size(size);

        Search search = new Search.Builder(sourceBuilder.toString()).addIndex(GmallConstant.ES_INDEX_SALE_DETAIL).addType(GmallConstant.ES_DEFAULT_TYPE).build();

        Map saleMap=new HashMap();
        try {
            SearchResult result = client.execute(search);

            //get detail data
            List<SearchResult.Hit<HashMap, Void>> hits = result.getHits(HashMap.class);
            ArrayList<HashMap> list = new ArrayList<>();

            for (SearchResult.Hit<HashMap, Void> hit : hits) {
                HashMap source = hit.source;
                list.add(source);
            }
            saleMap.put("detail",list);

            Map aggMap = new HashMap<>();

            List<TermsAggregation.Entry> buckets = result.getAggregations().getTermsAggregation("groupby_" + aggFieldName).getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                aggMap.put(bucket.getKey(),bucket.getCount());
            }

            saleMap.put("aggs",aggMap);

            saleMap.put("total",result.getTotal());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return saleMap;
    }
}
