package com.gmall.mock.utils.utils;

/**
 * @author star
 * @create 2019-04-01 17:35
 */
public class RanOpt<T> {
    T value ;
    int weight;

    public RanOpt(T value, int weight ){
        this.value=value ;
        this.weight=weight;
    }

    public T getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }

}
