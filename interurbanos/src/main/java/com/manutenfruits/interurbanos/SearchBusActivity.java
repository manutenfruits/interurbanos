package com.manutenfruits.interurbanos;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchBusActivity extends Activity {

    private static ArrayList<HashMap<String, String>> busLines;

    private static ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bus);

        handleIntent(getIntent());

        this.busLines = BusModel.getData();

        BusLinesAdapter adapter = new BusLinesAdapter(this, this.busLines);

        this.listView = (ListView) findViewById(R.id.buslist);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, String> item = (HashMap<String, String>)parent.getItemAtPosition(position);

            Intent intent = new Intent(SearchBusActivity.this, ScheduleActivity.class);
            intent.putExtra(BusModel.KEY_LINE, item.get(BusModel.KEY_GOING));
            startActivity(intent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY).toLowerCase();

            ArrayList<HashMap<String, String>> filtered = new ArrayList<HashMap<String, String>>();

            ArrayList<HashMap<String, String>> original = BusModel.getData();

            for(HashMap<String, String> busLine : original){
                String line = busLine.get(BusModel.KEY_LINE);
                if( line.toLowerCase().contains(query)){
                    filtered.add(busLine);
                }
            }

            this.busLines = filtered;
        }
    }
}
