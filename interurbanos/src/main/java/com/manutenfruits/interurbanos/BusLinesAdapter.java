package com.manutenfruits.interurbanos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by manutenfruits on 2/10/13.
 */
public class BusLinesAdapter extends BaseAdapter{

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    public BusLinesAdapter(Activity a, ArrayList<HashMap<String,String>> d){
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if(convertView == null){
            vi = inflater.inflate(R.layout.bus_line, null);
        }

        HashMap<String, String> busLine = data.get(position);

        TextView busNumber = (TextView) vi.findViewById(R.id.bus_number);
        TextView busOrigin = (TextView) vi.findViewById(R.id.bus_origin);
        TextView busDestination = (TextView) vi.findViewById(R.id.bus_destination);

        busNumber.setText(busLine.get(MainActivity.KEY_LINE));
        busOrigin.setText(busLine.get(MainActivity.KEY_ORIGIN));
        busDestination.setText(busLine.get(MainActivity.KEY_DESTINATION));

        String busType = busLine.get(MainActivity.KEY_BUSTYPE);
        if(busType.equals(MainActivity.NIGHTBUS)){
            busNumber.setTextColor(activity.getResources().getColor(R.color.nightbusfg));
            busNumber.setBackgroundColor(activity.getResources().getColor(R.color.nightbusbg));
        }else{
            busNumber.setTextColor(activity.getResources().getColor(R.color.regularbusfg));
            busNumber.setBackgroundColor(activity.getResources().getColor(R.color.regularbusbg));
        }

        return vi;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
}