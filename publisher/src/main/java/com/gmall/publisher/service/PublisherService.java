package com.gmall.publisher.service;

import java.util.Map;

/**
 * @author star
 * @create 2019-04-03 22:54
 */
public interface PublisherService {
    public Long getDauTotal(String date);
    public Long getNewMidTotal(String date);

    public Map getDauHours(String date);

    public Double getOrderAmount(String date);

    public Map getOrderAmountHour(String date);

    public Map getSaleDetail(String date ,String keyword,int startPage ,int size,String aggFieldName ,int aggsSize );
}
