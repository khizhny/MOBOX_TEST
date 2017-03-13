package com.khizhny.mobox;

import android.app.Application;
import android.app.job.JobScheduler;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    public static List<ListItem> itemsList =  new ArrayList<>();
    public static String JSON_URL="https://api.myjson.com/bins/txjov";
    public static boolean refreshNeeded=true;
    public static boolean serviceIsRunning=false;
    public static Drawable selectedIcon;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        //Cancel all background jobs
        JobScheduler tm = (JobScheduler) getSystemService(this.JOB_SCHEDULER_SERVICE);
        tm.cancelAll();
        super.onTerminate();
    }

}