package com.manutenfruits.interurbanos;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.manutenfruits.interurbanos.model.BusLine;
import com.manutenfruits.interurbanos.model.BusModel;
import com.manutenfruits.interurbanos.model.DiskLruImageCache;
import com.manutenfruits.interurbanos.view.FavoriteMenuButton;
import com.manutenfruits.interurbanos.view.ScheduleView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.net.URI;

public class ScheduleActivity extends Activity {

    private ScrollView scroll;
    private ScheduleView schedule;
    private View loading;
    private DiskLruImageCache dlic;
    private LinearLayout textMsg;
    private FavoriteMenuButton favBtn;

    private BusLine busLine;

    private boolean going;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        ActionBar actionBar = getActionBar();

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        actionBar.setDisplayHomeAsUpEnabled(true);

        View customActionBar = LayoutInflater.from(this).inflate(R.layout.schedule_actionbar, null);

        actionBar.setCustomView(customActionBar, lp);
        actionBar.setDisplayShowCustomEnabled(true);

        this.favBtn = (FavoriteMenuButton) customActionBar.findViewById(R.id.favoriteSchedule);

        this.scroll = (ScrollView) findViewById(R.id.scrollSchedule);
        this.loading = findViewById(R.id.loadingSchedule);
        this.schedule = (ScheduleView) findViewById(R.id.scheduleView);
        this.textMsg = (LinearLayout) findViewById(R.id.scheduleNotFound);

        this.busLine = getIntent().getParcelableExtra(BusLine.KEY);
        this.going = getIntent().getBooleanExtra(BusLine.DIRECTION, true);

        favBtn.setChecked(BusModel.isFavorite(busLine.getLine()));
        favBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    BusModel.addFavorite(busLine.getLine());
                }else{
                    BusModel.removeFavorite(busLine.getLine());
                }
            }
        });

        this.dlic = new DiskLruImageCache(this, BusModel.BUSLINES_CACHE, BusModel.DISK_CACHE_SIZE, Bitmap.CompressFormat.PNG, 100);

        new ScheduleRetriever().execute(busLine);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule, menu);

        MenuItem flipDirection = menu.findItem(R.id.flip_direction);

        if(this.going){
            flipDirection.setTitle(R.string.flip_to_coming);
        }else{
            flipDirection.setTitle(R.string.flip_to_going);
        }

        flipDirection.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(going){
                    item.setTitle(R.string.flip_to_coming);
                }else{
                    item.setTitle(R.string.flip_to_going);
                }
                going = !going;
                new ScheduleRetriever().execute(busLine);
                return true;
            }
        });

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void DrawSchedule(Bitmap bitmap){

        if(bitmap == null){
            schedule.setVisibility(View.GONE);
            textMsg.setVisibility(View.VISIBLE);
        }else{
            textMsg.setVisibility(View.GONE);
            schedule.setVisibility(View.VISIBLE);
            schedule.setImageBitmap(bitmap);
        }
    }

    private class ScheduleRetriever extends AsyncTask<BusLine, Void, Bitmap> {

        private String key;

        @Override
        protected Bitmap doInBackground(BusLine... params) {
            Bitmap schedule;
            this.key = busLine.getLine().toLowerCase() + "-" + (going?"1":"2");

            if(dlic.containsKey(key)){
                schedule = dlic.getBitmap(key);
            }else{
                URI uri = going? busLine.getGoing(): busLine.getComing();
                schedule = downloadBitmap(uri);

                if(schedule != null){
                    dlic.put(key, schedule);
                }
            }

            return schedule;
        }

        @Override
        protected void onPreExecute() {
            scroll.setVisibility(View.GONE);
            textMsg.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            loading.setVisibility(View.GONE);
            scroll.setVisibility(View.VISIBLE);
            scroll.scrollTo(0, 0);
            DrawSchedule(result);
        }

        private Bitmap downloadBitmap(URI url) {
            // initilize the default HTTP client object
            final DefaultHttpClient client = new DefaultHttpClient();

            //forming a HttoGet request
            final HttpGet getRequest = new HttpGet(url);
            try {

                HttpResponse response = client.execute(getRequest);

                //check 200 OK for success
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("ScheduleRetriever", "Error " + statusCode +
                            " while retrieving bitmap from " + url);
                    return null;
                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        // getting contents from the stream
                        inputStream = entity.getContent();

                        // decoding stream data back into image Bitmap that android understands
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        return bitmap;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                // You Could provide a more explicit error message for IOException
                getRequest.abort();
                Log.e("ScheduleRetriever", "Something went wrong while" +
                        " retrieving bitmap from " + url + e.toString());
            }

            return null;
        }
    }

}