package com.gmall.publisher.bean;

import java.util.List;

/**
 * @author star
 * @create 2019-04-15 11:35
 */
public class Stat {
    String title ;

    List<Option> options;

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
