package com.turu.model;

public class Audio {
    private String name;
    private String url;
    private int volume;

    // Constructor
    public Audio(String name, String url, int volume) {
        this.name = name;
        this.url = url;
        this.volume = volume;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
