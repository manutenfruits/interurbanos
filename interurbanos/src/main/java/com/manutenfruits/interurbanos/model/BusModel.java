package com.manutenfruits.interurbanos.model;

import android.content.SharedPreferences;

import com.jakewharton.disklrucache.DiskLruCache;
import com.manutenfruits.interurbanos.BusApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/**
 * Created by manutenfruits on 3/10/13.
 */
public class BusModel {

    public final static String BUSLINES_CACHE = "buscache";
    public static final int DISK_CACHE_SIZE = 1024 * 1024 * 1; // 10MB
    public static final String DISK_CACHE_SUBDIR = "cached";
    private final static String BUSLINES_FILE = "buslines.json";
    private final static String FAVORITES_PREFS = "favorites";
    private final static String FAVORITES_KEY = "favorites";

    private static final String KEY_GOING = "forward";
    private static final String KEY_COMING = "backward";
    private static final String KEY_LINE = "line";
    private static final String KEY_ORIGIN = "origin";
    private static final String KEY_DESTINATION = "destination";

    private static ArrayList<BusLine> busLines = null;
    private static ArrayList<String> favorites = null;

    private static DiskLruCache dlc;
    private final Object dlcLock = new Object();
    private boolean dlcStarting = true;

    public static ArrayList<BusLine> getData(){
        if(busLines == null){
             parseBusLines();
        }
        return busLines;
    }

    public static ArrayList<BusLine> getFavorites(){
        if(favorites == null){
            favorites = readFavorites();
        }

        ArrayList<BusLine> favBuses = new ArrayList<BusLine>();

        for(String busLine : favorites){
            int index = Collections.binarySearch(busLines, busLine);
            favBuses.add(busLines.get(index));
        }

        return favBuses;
    }

    private static String readBusLines(){
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

    private static void parseBusLines(){

        busLines = new ArrayList<BusLine>();

        String fileContent = readBusLines();

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
                return lhs.getLine().compareTo(rhs.getLine());
            }
        });
    }

    private static ArrayList<String> readFavorites(){
        SharedPreferences prefs = BusApplication.getInstance().getSharedPreferences(FAVORITES_PREFS, 0);
        HashSet<String> favoriteSet = (HashSet<String>) prefs.getStringSet(FAVORITES_KEY, new HashSet<String>());

        return new ArrayList<String>(favoriteSet);
    }

    private static void writeFavorites(ArrayList<String> favorites){
        SharedPreferences prefs = BusApplication.getInstance().getSharedPreferences(FAVORITES_PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        HashSet<String> favoriteSet = new HashSet<String>(favorites);

        editor.putStringSet(FAVORITES_KEY, favoriteSet);
        editor.commit();
    }

    public static boolean addFavorite(String busLine){
        if(!favorites.contains(busLine)){
            favorites.add(busLine);
            writeFavorites(favorites);
            return true;
        }
        return false;
    }

    public static boolean removeFavorite(String busLine){
        int index = favorites.indexOf(busLine);
        if(index >= 0){
            favorites.remove(index);
            writeFavorites(favorites);
            return true;
        }
        return false;
    }

    public static boolean isFavorite(String busLine){
        return favorites.contains(busLine);
    }

}
