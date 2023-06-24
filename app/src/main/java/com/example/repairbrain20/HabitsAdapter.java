package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Map;

public class HabitsAdapter extends BaseAdapter {

    Activity act;
    Map<String,Object> map;
    List<Object> map_keys;
    String[] days_list = {"Sun","Mon","Tue","Wed","Thur","Fri","Sat"};
    String today;

    HabitsAdapter(Activity act, Map<String,Object> map,List<Object> keys)
    {
        this.act = act;
        this.map = map;
        map_keys = keys;
        today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("E"));
    }

    @Override
    public int getCount() {
        return map_keys.size();
    }

    @Override
    public Object getItem(int i) {
        return map_keys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = act.getLayoutInflater().inflate(R.layout.custom_habits_list,null,false);

        String key = map_keys.get(i).toString();

        TextView text = view.findViewById(R.id.habit);
        TextView show = view.findViewById(R.id.show_on);

        Log.e("sanjay_key",map.get(key).toString());

        Map<String,Object> habit_data = (Map<String, Object>) map.get(key);
        List<Object> show_on = (List<Object>) habit_data.get("show_on");

        String days = "";

        for(String day:days_list)
        {
           if(show_on.contains(day)) days+=day+" ";
           if(!show_on.contains(today))
           {
               view.setBackgroundColor(Color.LTGRAY);
               Log.e("sanjay_color",day);
           }
        }

        text.setText(key);
        show.setText(days);

        return view;
    }
}
