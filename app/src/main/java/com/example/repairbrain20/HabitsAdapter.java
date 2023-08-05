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

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HabitsAdapter extends BaseAdapter {

    Activity act;

    String[] days_list = {"Sun","Mon","Tue","Wed","Thur","Fri","Sat"};
    String today;
    int count = 0;
    TextView percent = null;
    ImageView up_or_down = null;
    List<String> keys;
    Map<String,ReplaceHabits> habits;
    String today_date;
    static int current_percent = 0;
    static Map<String,ReplaceHabits> habits_copy;

    HabitsAdapter(Activity act, Map<String,ReplaceHabits> habits)
    {
        this.percent = act.findViewById(R.id.percent);
        this.up_or_down = act.findViewById(R.id.up_or_down);
        this.habits = habits;
        this.act = act;

        habits_copy = habits;

        today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("E"));
        keys = new ArrayList<>(habits.keySet());

        LocalDateTime date_time = LocalDateTime.now();
        today_date = DateTimeFormatter.ofPattern("dd-MM-yy").format(date_time);
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @Override
    public Object getItem(int i) {
        return keys.get(i);
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

        String key = keys.get(i);

       check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                update_percentage(b);
                update_value(key,b);
            }
        });

        ReplaceHabits habits = this.habits.get(key);

        List<String> show_on = habits.getShow_on();
        Map<String,Integer> days_data = habits.getDays_data();

        StringBuilder builder = new StringBuilder();

        for(String day : days_list)
        {
            if(show_on.contains(day))
            {
                builder.append(day).append(",").append(" ");
            }
        }

        String show_on_string = builder.deleteCharAt(builder.lastIndexOf(" ")).deleteCharAt(builder.lastIndexOf(",")).toString();

        text.setText(key);
        show.setText(show_on_string);

        return view;
    }

    public void update_value(String key,Boolean b)
    {
        User.getReference()
                .child("replace_habits")
                .child(key)
                .child("days_data")
                .child(today_date)
                .setValue(b?1:0);
    }

    public void update_percentage(boolean b)
    {
        if(b) ++count; else --count;

        int size = keys.size();

        if(size==0) return;

        int percent = (count * 100) / size;
        this.percent.setText(percent+"%");

        current_percent = percent;

        int diff = percent - HabitsAndAccuracy.last_accuracy_percent;

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
