package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

    Activity activity;
    String[] list;

    ListAdapter(Activity act,String text)
    {
        this.activity = act;
        list = text.split("\n");
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public String getItem(int i) {
        return list[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.custom_list_effects,null,true);

        TextView text_widget = view.findViewById(R.id.text);
        TextView date_widget = view.findViewById(R.id.date);

        String text = list[i];
        String date = text.replaceAll("[()]","").trim();
        String habit = text.replace("("+date+")","").trim();

        Log.e("sanjay_date",date);
        Log.e("sanjay_habit",habit);

        text_widget.setText(habit);
        date_widget.setText(date);

        return view;
    }
}
