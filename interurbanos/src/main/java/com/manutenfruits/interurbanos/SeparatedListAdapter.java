package com.manutenfruits.interurbanos;

import android.content.Context;
import android.opengl.Visibility;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.manutenfruits.interurbanos.model.BusLine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SeparatedListAdapter extends BaseAdapter{

    public final Map<String,BusLinesAdapter> sections = new LinkedHashMap<String,BusLinesAdapter>();
    public final ArrayAdapter<String> headers;
    public final static int TYPE_SECTION_HEADER = 0;

    public SeparatedListAdapter(Context context) {
        headers = new ArrayAdapter<String>(context, R.layout.separator);
    }

    public BusLinesAdapter addSection(String section, BusLinesAdapter adapter) {
        this.headers.add(section);
        this.sections.put(section, adapter);
        return adapter;
    }

    public void clear(){
        for(Object section : this.sections.keySet()) {
            sections.get(section).setSelected(-1);
        }
        notifyDataSetChanged();
    }



    public Object getItem(int position) {
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            if(size > 1){
                // check if position inside this section
                if(position == 0) return section;
                if(position < size) return adapter.getItem(position - 1);

                // otherwise jump into next section
                position -= size;
            }
        }
        return null;
    }

    public int getCount() {
        // total together all sections, plus one for each section header
        int total = 0;
        for(Adapter adapter : this.sections.values())
            if(adapter.getCount() > 0)
                total += adapter.getCount() + 1;
        return total;
    }

    public int getViewTypeCount() {
        // assume that headers count as one, then total all sections
        int total = 1;
        for(Adapter adapter : this.sections.values())
            if(adapter.getCount() > 0)
                total += adapter.getViewTypeCount();
        return total;
    }

    public int getItemViewType(int position) {
        int type = 1;
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            if(size > 1){
                // check if position inside this section
                if(position == 0) return TYPE_SECTION_HEADER;
                if(position < size) return type + adapter.getItemViewType(position - 1);

                // otherwise jump into next section
                position -= size;
                type += adapter.getViewTypeCount();
            }
        }
        return -1;
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionnum = 0;
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            if(size > 1){
                // check if position inside this section
                if(position == 0) return headers.getView(sectionnum, convertView, parent);
                if(position < size) return adapter.getView(position - 1, convertView, parent);

                // otherwise jump into next section
                position -= size;
            }
            sectionnum++;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void filter(CharSequence constraint) {

        for(Object section : sections.keySet()) {
            BusLinesAdapter adapter = sections.get(section);
            adapter.getFilter().filter(constraint);
        }

    }
}
