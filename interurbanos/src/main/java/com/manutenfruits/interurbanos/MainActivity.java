package com.manutenfruits.interurbanos;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private final static  String BUSLINES_FILE = "buslines.json";

    private static ArrayList<HashMap<String, String>> busLines;

    static final String KEY_GOING = "forward";
    static final String KEY_COMING = "backward";
    static final String KEY_LINE = "line";
    static final String KEY_ORIGIN = "origin";
    static final String KEY_DESTINATION = "destination";

    static final String KEY_BUSTYPE = "bustype";

    static final String REGULARBUS = "regularbus";
    static final String NIGHTBUS = "nightbus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initList();

        BusLinesAdapter adapter = new BusLinesAdapter(this, busLines);

        ListView listView = (ListView) findViewById(R.id.buslist);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> item = (HashMap<String, String>)parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                intent.putExtra(KEY_LINE, item.get(KEY_GOING));
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private String readData(){
        String content = null;
        try{

            InputStream is = getAssets().open(BUSLINES_FILE);
            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            content = new String(buffer, "UTF-8");

        }catch(IOException ex){
            ex.printStackTrace();
            return null;
        }

        return content;
    }

    private void initList(){

        busLines = new ArrayList<HashMap<String, String>>();

        String fileContent = readData();

        try{
            JSONArray json = new JSONArray(fileContent);

            for(int i = 0; i<json.length();i++){
                JSONObject jsonChildNode = json.getJSONObject(i);

                HashMap busLine = new HashMap<String, String>();

                busLine.put(KEY_GOING, jsonChildNode.optString(KEY_GOING));
                busLine.put(KEY_COMING, jsonChildNode.optString(KEY_COMING));
                busLine.put(KEY_LINE, jsonChildNode.optString(KEY_LINE));
                busLine.put(KEY_ORIGIN, jsonChildNode.optString(KEY_ORIGIN));
                busLine.put(KEY_DESTINATION, jsonChildNode.optString(KEY_DESTINATION));
                if(jsonChildNode.optString(KEY_LINE).charAt(0) == 'N')
                    busLine.put(KEY_BUSTYPE, NIGHTBUS);
                else
                    busLine.put(KEY_BUSTYPE, REGULARBUS);

                busLines.add(busLine);
            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

        Collections.sort(busLines, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                return (lhs.get(KEY_LINE).compareTo(rhs.get(KEY_LINE)));
            }
        });
    }

}