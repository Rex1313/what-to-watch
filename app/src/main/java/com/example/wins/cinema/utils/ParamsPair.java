package com.example.wins.cinema.utils;

/**
 * Created by wins on 10/16/2016.
 */
public class ParamsPair {
    private String key;
    private String value;

    public ParamsPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
