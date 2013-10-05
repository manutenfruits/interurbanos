package com.manutenfruits.interurbanos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by manutenfruits on 3/10/13.
 */
public class BusModel {

    private final static  String BUSLINES_FILE = "buslines.json";

    static final String KEY_GOING = "forward";
    static final String KEY_COMING = "backward";
    static final String KEY_LINE = "line";
    static final String KEY_ORIGIN = "origin";
    static final String KEY_DESTINATION = "destination";

    static final String KEY_BUSTYPE = "bustype";

    static final String REGULARBUS = "regularbus";
    static final String NIGHTBUS = "nightbus";

    private static ArrayList<HashMap<String, String>> busLines = null;

    public static ArrayList<HashMap<String, String>> getData(){
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
