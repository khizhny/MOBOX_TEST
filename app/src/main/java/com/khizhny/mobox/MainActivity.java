package com.khizhny.mobox;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends Activity {


    public static final String MESSENGER_INTENT_KEY = BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";
    public static final int MSG_START = 1;

    private static int kJobId = 0;
    private RecyclerListAdapter recyclerListAdapter;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerListAdapter = new RecyclerListAdapter();
        mRecyclerView.setAdapter(recyclerListAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(recyclerListAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        if (MyApplication.refreshNeeded) {
            JsonParseTask task = new JsonParseTask();
            task.execute();
        }
        if (!MyApplication.serviceIsRunning) {
            // Schedule job
            ComponentName serviceComponent = new ComponentName(this, TimeService.class);
            JobInfo.Builder builder = new JobInfo.Builder(kJobId++, serviceComponent);
            builder.setPeriodic(2*60*1000); // each 2 min
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setRequiresCharging(false);
            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
    }
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
/*
    @Override
    public void update(Observable o, Object arg) {
        Toast.makeText(this, String.valueOf("activity observer "), Toast.LENGTH_SHORT).show();
    }*/

    class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
            implements ItemTouchHelperAdapter    {

         class ItemViewHolder extends RecyclerView.ViewHolder implements
                ItemTouchHelperViewHolder {
             private TextView mTextView;
             private ImageView mImageView;
             private ItemViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.small_text);
                mImageView = (ImageView) v.findViewById(R.id.small_image);
            }

            @Override
            public void onItemSelected() {
                itemView.setBackgroundColor(Color.LTGRAY);
            }

            @Override
            public void onItemClear() {
                itemView.setBackgroundColor(0);
            }
        }



        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            ListItem prev = MyApplication.itemsList.remove(fromPosition);
            MyApplication.itemsList.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {
            MyApplication.itemsList.remove(position);
            notifyItemRemoved(position);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View rowView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_layout , parent, false);
            ItemViewHolder vh = new ItemViewHolder(rowView);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, final int position) {

            holder.mTextView.setText(MyApplication.itemsList.get(position).name);

            // downloading images
            String url = MyApplication.itemsList.get(position).url;
            ImageRequest imageRequest = new ImageRequest(url,holder.mImageView,true);
            ImageDownloaderTask task  = new ImageDownloaderTask();
            task.execute(imageRequest);

            // define a click listener
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra("name",MyApplication.itemsList.get(position).name);
                    intent.putExtra("url", MyApplication.itemsList.get(position).url);
                    final ImageView smallImageView = (ImageView) view.findViewById(R.id.small_image);
                    MyApplication.selectedIcon = smallImageView.getDrawable();
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(MainActivity.this, smallImageView, "shared_element_transition");
                    // start the new activity
                    startActivity(intent, options.toBundle());
                }
            });/**/

            // define swipe

        }

        @Override
        public int getItemCount() {
            return MyApplication.itemsList.size();
        }

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(MainActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(MainActivity.this, "on Swiped ", Toast.LENGTH_SHORT).show();
                //Remove swiped item from list and notify the RecyclerView
            }
        };
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class JsonParseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Wait. I'am parsing JSON...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(MyApplication.JSON_URL);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray items = jsonObj.getJSONArray("items");

                    // looping through All Items
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);
                        String name = c.getString("name");
                        String url = c.getString("url");
                        MyApplication.itemsList.add(new ListItem(name,url));
                    }
                } catch (final JSONException e) {}

            } else {}

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            MyApplication.refreshNeeded=false;
            recyclerListAdapter.notifyDataSetChanged();
        }

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Dialog timeDialog = new Dialog(MainActivity.this);
            timeDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            timeDialog.setContentView(getLayoutInflater().inflate(R.layout.time_alert, null));
            //setting time
            TextView tv = (TextView) timeDialog.findViewById(R.id.time_text);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String currentDateandTime = sdf.format(new Date());
            tv.setText("Текущее время - "+currentDateandTime);

            //settimg image
            ImageView iv =(ImageView) timeDialog.findViewById(R.id.random_image);

            //Start random image download
            Random rnd = new Random();
            int randomIndex = rnd.nextInt(MyApplication.itemsList.size());
            String randomUrl = MyApplication.itemsList.get(randomIndex).url;
            ImageRequest imageRequest = new ImageRequest(randomUrl,iv,false);
            ImageDownloaderTask task  = new ImageDownloaderTask();
            task.execute(imageRequest);

            timeDialog.show();

        }
    };


}
