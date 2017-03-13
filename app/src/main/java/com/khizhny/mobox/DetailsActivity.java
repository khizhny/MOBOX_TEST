package com.khizhny.mobox;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        TextView bigText = (TextView) findViewById(R.id.big_text);
        String name= getIntent().getStringExtra("name");
        String url= getIntent().getStringExtra("url");
        bigText.setText(name);


        ImageView mainImage=(ImageView)findViewById(R.id.big_image);
        mainImage.setImageDrawable(MyApplication.selectedIcon);
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition));


        // downloading big image
        ImageRequest imageRequest = new ImageRequest(url,mainImage,false);
        ImageDownloaderTask task  = new ImageDownloaderTask();
        task.execute(imageRequest);
    }
    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimeService.BROADCAST_ACTION);
        registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Dialog timeDialog = new Dialog(DetailsActivity.this);
            timeDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            timeDialog.setContentView(getLayoutInflater().inflate(R.layout.time_alert, null));
            //setting time
            TextView tv = (TextView) timeDialog.findViewById(R.id.time_text);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String currentDateandTime = sdf.format(new Date());
            tv.setText("Текущее время - "+currentDateandTime);

            //Start random image download
            Random rnd = new Random();
            int randomIndex = rnd.nextInt(MyApplication.itemsList.size());
            String randomUrl = MyApplication.itemsList.get(randomIndex).url;
            ImageView iv =(ImageView) timeDialog.findViewById(R.id.random_image);
            ImageRequest imageRequest = new ImageRequest(randomUrl,iv,false);
            ImageDownloaderTask task  = new ImageDownloaderTask();
            task.execute(imageRequest);

            timeDialog.show();
        }
    };
}
