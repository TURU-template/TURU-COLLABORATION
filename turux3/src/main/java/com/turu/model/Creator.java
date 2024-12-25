package com.turu.model;

public class Creator {
    private String icon;
    private String name;
    private String link;

    public Creator(String icon, String name, String link) {
        this.icon = icon;
        this.name = name;
        this.link = link;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}