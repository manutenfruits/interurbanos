package com.manutenfruits.interurbanos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.manutenfruits.interurbanos.model.BusLine;
import com.manutenfruits.interurbanos.model.BusModel;

import java.util.ArrayList;

/**
 * Created by manutenfruits on 2/10/13.
 */
public class BusLinesAdapter extends BaseAdapter implements Filterable{

    private Activity activity;
    private ArrayList<BusLine> original;
    private ArrayList<BusLine> data;
    private static LayoutInflater inflater = null;
    private SeparatedListAdapter parent;

    private int selected = -1;

    public BusLinesAdapter(Activity a, ArrayList<BusLine> d, SeparatedListAdapter parent){
        this.activity = a;
        this.original = d;
        this.data = d;
        this.parent = parent;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDataSet(ArrayList<BusLine> d){
        this.data = d;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if(convertView == null){
            vi = inflater.inflate(R.layout.bus_line, null);
        }

        BusLine busLine = data.get(position);

        TextView busNumber = (TextView) vi.findViewById(R.id.bus_number);
        TextView busOrigin = (TextView) vi.findViewById(R.id.bus_origin);
        TextView busDestination = (TextView) vi.findViewById(R.id.bus_destination);
        TextView busGoing = (TextView) vi.findViewById(R.id.bus_going);
        TextView busComing = (TextView) vi.findViewById(R.id.bus_coming);

        LinearLayout busLabel = (LinearLayout) vi.findViewById(R.id.bus_label);
        LinearLayout busDescription = (LinearLayout) vi.findViewById(R.id.bus_description);

        String line = busLine.getLine();
        busNumber.setText(line);
        busOrigin.setText(busLine.getOrigin());
        busDestination.setText(busLine.getDestination());

        if(busLine.isNightBus()){
            busNumber.setTextColor(activity.getResources().getColor(R.color.nightbusfg));
            busNumber.setBackgroundColor(activity.getResources().getColor(R.color.nightbusbg));
        }else{
            busNumber.setTextColor(activity.getResources().getColor(R.color.regularbusfg));
            busNumber.setBackgroundColor(activity.getResources().getColor(R.color.regularbusbg));
        }

        if(BusModel.isFavorite(line)){
            busNumber.setTextColor(activity.getResources().getColor(R.color.gold));
        }

        if(selected == position){
            select(vi);
        }else{
            unselect(vi);
        }

        busDescription.setOnClickListener(new SelectClickListener(position, true));
        busLabel.setOnClickListener(new SelectClickListener(position, false));

        busGoing.setOnClickListener(new GoingComingClickListener(position, true));
        busComing.setOnClickListener(new GoingComingClickListener(position, false));

        return vi;
    }

    private void select(View v){
        v.findViewById(R.id.bus_description).setVisibility(View.GONE);
        v.findViewById(R.id.bus_selector).setVisibility(View.VISIBLE);
    }

    private void unselect(View v){
        v.findViewById(R.id.bus_selector).setVisibility(View.GONE);
        v.findViewById(R.id.bus_description).setVisibility(View.VISIBLE);
    }

    public void setSelected(int selected){
        this.selected = selected;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if(constraint == null || constraint.length() == 0){
                    results.values = original;
                    results.count = original.size();
                }else{
                    String token = constraint.toString().toLowerCase();
                    ArrayList<BusLine> filtered = new ArrayList<BusLine>();

                    for(BusLine data: original){
                        if(data.getLine().toLowerCase().contains(token) ||
                                data.getOrigin().toLowerCase().contains(token) ||
                                data.getDestination().toLowerCase().contains(token) ){
                            filtered.add(data);
                        }
                    }

                    results.values = filtered;
                    results.count = filtered.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (ArrayList<BusLine>) results.values;
                parent.clear();
            }
        };
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class SelectClickListener implements View.OnClickListener{

        private int position;
        private boolean reveal;

        public SelectClickListener(int position, boolean reveal){
            this.position = position;
            this.reveal = reveal;
        }

        @Override
        public void onClick(View v) {
            View parentView;
            if(this.reveal){
                parentView = (View) v.getParent().getParent();
            }else{
                parentView = (View) v.getParent();
            }

            if(this.reveal){
                int previous = selected;
                parent.clear();
                select(parentView);
                selected = this.position;
            }else{
                unselect(parentView);

                if(selected == this.position){
                    selected = -1;
                }
            }
        }
    }

    public class GoingComingClickListener implements View.OnClickListener {

        private int position;
        private boolean going;

        public GoingComingClickListener(int position, boolean going) {
            this.position = position;
            this.going = going;
        }

        @Override
        public void onClick(View v) {

            BusLine item = data.get(this.position);

            Intent intent = new Intent(v.getContext(), ScheduleActivity.class);

            intent.putExtra(BusLine.KEY, item);
            intent.putExtra(BusLine.DIRECTION, this.going);

            v.getContext().startActivity(intent);
        }
    }

    @Override
    public Object getItem(int position) {
        return this.data.get(position);
    }
}