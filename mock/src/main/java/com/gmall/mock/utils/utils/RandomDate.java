package com.gmall.mock.utils.utils;

import java.util.Date;
import java.util.Random;

/**
 * @author star
 * @create 2019-04-01 17:31
 */
public class RandomDate {
    Long logDateTime = 0l;
    int maxTimeStamp = 0;

    public RandomDate(Date startDate, Date endDate, int num) {
        Long avgStepTime = (endDate.getTime()-startDate.getTime())/num;
        this.maxTimeStamp = avgStepTime.intValue()*2;
        this.logDateTime = startDate.getTime();
    }

    public Date getRandomDate() {
        int timeStamp = new Random().nextInt(maxTimeStamp);
        logDateTime = logDateTime+timeStamp;
        return new Date(logDateTime);
    }
}
