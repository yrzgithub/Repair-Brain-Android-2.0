package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterTriggers extends BaseAdapter {
    Activity act;
    Map<String,Trigger> triggers = new HashMap<>();
    static Map<String,Trigger> triggers_copy = new HashMap<>();
    List<String> keys = new ArrayList<>();

    AdapterTriggers(Activity activity, Map<String,Trigger> map)
    {
        this.act = activity;
        triggers.putAll(map);

        keys.addAll(map.keySet());
        triggers_copy = map;
    }

    @Override
    public int getCount() {
        return triggers.size();
    }

    @Override
    public Object getItem(int i) {
        return triggers.get(keys.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view =  act.getLayoutInflater().inflate(R.layout.custom_triggers_list,null);

        RelativeLayout main = view.findViewById(R.id.main);
        TextView trigger_name = view.findViewById(R.id.trigger_name);
        TextView date_added = view.findViewById(R.id.date_added);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        String trigger = keys.get(i);

        Trigger trigger_object = triggers.get(trigger);

        String added_on = trigger_object.getDate_added();

        trigger_name.setText(trigger);
        date_added.setText(added_on);

        return view;
    }
}