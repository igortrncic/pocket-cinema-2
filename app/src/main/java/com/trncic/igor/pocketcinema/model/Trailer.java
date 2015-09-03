package com.trncic.igor.pocketcinema.model;

import java.io.Serializable;

/**
 * Created by igortrncic on 7/6/15.
 */
public class Trailer implements Serializable {
    private String key;
    private String name;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String generateYoutubeLink() {
        return "https://www.youtube.com/watch?v=" + key;
    }
}
