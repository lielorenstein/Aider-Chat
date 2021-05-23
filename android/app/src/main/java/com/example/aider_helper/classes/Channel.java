package com.example.aider_helper.classes;

public class Channel {
    private String id;
    private int numHelpers = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Channel(String id) {
        this.id = id;
    }
}
