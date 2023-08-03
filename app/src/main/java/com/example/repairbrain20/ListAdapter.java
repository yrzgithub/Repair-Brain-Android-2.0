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

public class ListAdapter extends BaseAdapter {

    Activity activity;
    Map<String,Integer> map;
    List<String> keys;

    ListAdapter(Activity act,ReplaceHabits habits)
    {
        this.activity = act;
        this.map = habits.getDays_data();
        this.keys = habits.getShow_on();

        Log.e("sanjay_cute",keys.toString());
        Log.e("sanjay_cute",map.toString());
    }

    @Override
    public int getCount() {
        Log.e("sanjay_map",String.valueOf(map.size()));
        return map.size();
    }

    @Override
    public String getItem(int i) {
       // Log.e("sanjay_map",String.valueOf(i));
        return map.get(keys.get(i)).toString();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.custom_list_effects,null,false);

        TextView text_widget = view.findViewById(R.id.text);
        TextView date_widget = view.findViewById(R.id.date);

        text_widget.setSelected(true);

        String key = keys.get(i).toString();
        String value = map.get(key).toString();

        text_widget.setText(key);
        date_widget.setText(value);

        return view;
    }
}
