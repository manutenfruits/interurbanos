package com.manutenfruits.interurbanos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by manutenfruits on 3/10/13.
 */
public class BusModel {

    private final static  String BUSLINES_FILE = "buslines.json";

    private static final String KEY_GOING = "forward";
    private static final String KEY_COMING = "backward";
    private static final String KEY_LINE = "line";
    private static final String KEY_ORIGIN = "origin";
    private static final String KEY_DESTINATION = "destination";

    private static ArrayList<BusLine> busLines = null;

    public static ArrayList<BusLine> getData(){
        if(busLines == null){
            initList();
        }

        return busLines;
    }

    private static String readData(){
        String content = null;
        try{

            InputStream is = BusApplication.getInstance().getAssets().open(BUSLINES_FILE);
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

    private static void initList(){

        busLines = new ArrayList<BusLine>();

        String fileContent = readData();

        try{
            JSONArray json = new JSONArray(fileContent);

            for(int i = 0; i<json.length();i++){
                JSONObject jsonChildNode = json.getJSONObject(i);

                String going = jsonChildNode.optString(KEY_GOING);
                String coming = jsonChildNode.optString(KEY_COMING);
                String line = jsonChildNode.optString(KEY_LINE);
                String origin = jsonChildNode.optString(KEY_ORIGIN);
                String destination = jsonChildNode.optString(KEY_DESTINATION);

                try{
                    BusLine busLine = new BusLine(line, origin, destination, going, coming);
                    busLines.add(busLine);
                }catch(URISyntaxException e){
                    e.printStackTrace();
                }

            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }

        Collections.sort(busLines, new Comparator<BusLine>() {
            @Override
            public int compare(BusLine lhs, BusLine rhs) {
                return (lhs.getLine().compareTo(rhs.getLine()));
            }
        });
    }
}
