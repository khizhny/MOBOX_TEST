package com.khizhny.mobox;

public final class ListItem {

    public final String name;
    public final String url;

    public ListItem(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
