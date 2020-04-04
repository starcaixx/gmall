package com.gmall.constant;

/**
 * @author star
 * @create 2019-04-01 18:48
 */
public class GmallConstant {

    public final static String TOPIC_STARTUP = "GMALL_STARTUP";
    public final static String TOPIC_EVENT = "GMALL_EVENT";

    public static final String TOPIC_ORDER="GMALL_ORDER";
    public static final String KAFKA_TOPIC_NEW_ORDER="GMALL_NEW_ORDER";
    public static final String KAFKA_TOPIC_ORDER_DETAIL="GMALL_ORDER_DETAIL";

    public static final String ES_INDEX_DAU="gmall_dau";
//    public static final String ES_INDEX_NEW_MID="gmall_new_mid";
    public static final String ES_INDEX_NEW_ORDER="gmall_new_order";
    public static final String ES_INDEX_SALE_DETAIL="gmall_sale_detail";
    public static final String ES_HOST = "http://hadoop59";
    public static final int ES_PORT = 9200;
    public static final String ES_DEFAULT_TYPE="_doc";


}
