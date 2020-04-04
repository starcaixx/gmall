package com.gmall.mock.utils.utils;

import java.util.Random;

/**
 * @author star
 * @create 2019-04-01 17:37
 */
public class RandomNum {
    public static final  int getRandInt(int fromNum,int toNum){
        return   fromNum+ new Random().nextInt(toNum-fromNum+1);
    }
}
