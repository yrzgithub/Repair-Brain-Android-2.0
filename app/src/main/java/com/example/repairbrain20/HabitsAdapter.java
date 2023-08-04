package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Map;

public class HabitsAdapter extends BaseAdapter {

    Activity act;
    Map<String,Integer> map;
    List<String> map_keys;
    String[] days_list = {"Sun","Mon","Tue","Wed","Thur","Fri","Sat"};
    String today;
    int count = 0;
    TextView percent = null;
    ImageView up_or_down = null;

    HabitsAdapter(Activity act, ReplaceHabits replace_habits)
    {
        this.act = act;
        this.map = replace_habits.getDays_data();
        map_keys = replace_habits.getShow_on();
        today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("E"));
        this.percent = act.findViewById(R.id.percent);
        this.up_or_down = act.findViewById(R.id.up_or_down);
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

        TextView text = view.findViewById(R.id.habit);
        TextView show = view.findViewById(R.id.show_on);
        CheckBox check = view.findViewById(R.id.check);

        return view;
    }

    public void update_percentage(boolean b)
    {
        if(b) ++count; else --count;

        int size = map_keys.size();
        int percent = (count * 100) / size;
        this.percent.setText(percent+"%");

        int diff = percent - Habits.lastly_accuracy();
        // diff = 0;

        if(diff==0)
        {
            up_or_down.setImageBitmap(null);
        }
        else if(diff>0)
        {
            up_or_down.setImageResource(R.drawable.up);
        }
        else {
            up_or_down.setImageResource(R.drawable.down);
        }
    }
}
