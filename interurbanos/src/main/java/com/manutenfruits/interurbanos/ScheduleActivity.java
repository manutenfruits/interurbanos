package com.manutenfruits.interurbanos;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

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

    private BusLine busLine;

    private boolean going;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        this.scroll = (ScrollView) findViewById(R.id.scrollSchedule);
        this.loading = findViewById(R.id.loadingSchedule);
        this.schedule = (ScheduleView) findViewById(R.id.scheduleView);

        this.busLine = getIntent().getParcelableExtra(BusLine.KEY);
        this.going = getIntent().getBooleanExtra(BusLine.DIRECTION, true);

        URI scheduleURI;

        if(this.going){
            scheduleURI = this.busLine.getGoing();
        }else{
            scheduleURI = this.busLine.getComing();
        }

        new ImageDownloader().execute(scheduleURI);

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
                    new ImageDownloader().execute(busLine.getComing());
                    item.setTitle(R.string.flip_to_going);
                }else{
                    new ImageDownloader().execute(busLine.getGoing());
                    item.setTitle(R.string.flip_to_coming);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private class ImageDownloader extends AsyncTask<URI, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(URI... params) {
            // TODO Auto-generated method stub
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPreExecute() {
            scroll.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            loading.setVisibility(View.GONE);

            Drawable drawable = new BitmapDrawable(getResources(), result);
            scroll.setVisibility(View.VISIBLE);
            scroll.scrollTo(0, 0);
            schedule.setImageDrawable(drawable);
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
                    Log.w("ImageDownloader", "Error " + statusCode +
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
                Log.e("ImageDownloader", "Something went wrong while" +
                        " retrieving bitmap from " + url + e.toString());
            }

            return null;
        }
    }

}