package com.gmall.publisher.bean;

/**
 * @author star
 * @create 2019-04-15 11:35
 */
public class Option {
    String name ;
    Double value ;

    public Option(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
