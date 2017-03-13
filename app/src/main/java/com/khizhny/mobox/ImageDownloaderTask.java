package com.khizhny.mobox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloaderTask extends AsyncTask<ImageRequest,ImageRequest, Bitmap> {

    ImageView imageView = null;
    String url = null;
    boolean isSmall = true;

    @Override
    protected Bitmap doInBackground(ImageRequest... params) {
        this.imageView = params[0].imageView;
        this.url = params[0].url;
        this.isSmall=params[0].isSmall;
        return download_Image(this.url);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);

    }

    private Bitmap download_Image(String url) {
        Bitmap bmp =null;
        try{
            URL ulrn = new URL(url);
            HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
            InputStream is = con.getInputStream();
            bmp = BitmapFactory.decodeStream(is);

            if (null != bmp)
                if (isSmall) {
                    return getResizedBitmap(bmp,200,200);
                }else {
                    return getResizedBitmap(bmp,500,500);
                }
        }catch(Exception e){}
        return bmp;
    }

    private  Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }
}