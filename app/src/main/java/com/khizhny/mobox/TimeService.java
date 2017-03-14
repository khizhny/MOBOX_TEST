package com.khizhny.mobox;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

public class TimeService  extends JobService {
    public static String BROADCAST_ACTION = "com.khizhny.mobox.SHOWTIME";

    @Override
    public boolean onStartJob(JobParameters params) {
        // sending broadcast intent
        Intent broadcast = new Intent();
        broadcast.putExtra("FLAG","RED");
        broadcast.setAction(BROADCAST_ACTION);
        sendBroadcast(broadcast);
        Log.d("TimeService","Broadcast is SENT");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}