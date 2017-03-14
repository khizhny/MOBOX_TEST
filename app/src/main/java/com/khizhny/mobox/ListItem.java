package com.khizhny.mobox;

import android.graphics.drawable.Drawable;

public final class ListItem {

    public final String name;
    public final String url;
    public Drawable bigImage;
    public Drawable smallImage;

    public ListItem(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
