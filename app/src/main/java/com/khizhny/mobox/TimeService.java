package com.khizhny.mobox;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class TimeService  extends JobService {
    public static String BROADCAST_ACTION = "com.khizhny.mobox.SHOWTIME";

    @Override
    public boolean onStartJob(JobParameters params) {
        // sending broadcast intent
        Intent broadcast = new Intent();
        broadcast.setAction(BROADCAST_ACTION);
        sendBroadcast(broadcast);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}