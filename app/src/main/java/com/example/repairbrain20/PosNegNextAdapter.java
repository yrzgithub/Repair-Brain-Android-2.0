package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PosNegNextAdapter extends BaseAdapter {

    Activity activity;
    Map<String,String> map;

    PosNegNextAdapter(Activity act,Map<String,String> map)
    {
        this.activity = act;
        this.map = map;

        new ArrayList<>(map.keySet());

        Log.e("sanjay_cute",map.toString());
    }

    @Override
    public int getCount() {
        Log.e("sanjay_map",String.valueOf(map.size()));
        return map.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.custom_list_effects,null,false);

        TextView text_widget = view.findViewById(R.id.text);
        TextView date_widget = view.findViewById(R.id.date);

        text_widget.setSelected(true);

        return view;
    }
}

