package com.trncic.igor.pocketcinema.model;

import java.io.Serializable;

/**
 * Created by igortrncic on 9/3/15.
 */
public class Review implements Serializable {
    private String author;
    private String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
