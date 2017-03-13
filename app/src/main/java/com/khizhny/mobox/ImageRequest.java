package com.khizhny.mobox;

import android.widget.ImageView;

public class ImageRequest {
    public final String url;
    public final ImageView imageView;
    public final boolean isSmall;

    ImageRequest(String url, ImageView imageView, boolean isSmall){
        this.url=url;
        this.imageView=imageView;
        this.isSmall=isSmall;
    }
}
