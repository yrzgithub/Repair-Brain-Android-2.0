package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterRelapses extends BaseAdapter {

    Activity activity;
    Map<String, Relapse> relapses = new HashMap<>();
    List<String> keys = new ArrayList<>();

    AdapterRelapses(Activity act, View view, Map<String, Relapse> map) {
        this.activity = act;

        ImageView loading = view.findViewById(R.id.loading);
        ListView list = view.findViewById(R.id.list);

        if (map != null && map.size() > 0) {
            loading.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            relapses.putAll(map);
            keys.addAll(map.keySet());
        } else {
            Glide.with(activity).load(R.drawable.noresultfound).into(loading);
        }

    }

    @Override
    public int getCount() {
        return relapses.size();
    }

    @Override
    public Object getItem(int i) {
        return relapses.get(keys.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(activity).inflate(R.layout.custom_relapse_list, null);

        TextView relapse = view.findViewById(R.id.relapses);
        TextView time_gone = view.findViewById(R.id.time_gone);

        String key = keys.get(i);
        Relapse relapse_ = relapses.get(key);

        String date = relapse_.getDate();
        String progress = relapse_.getProgress();

        relapse.setText(date);
        time_gone.setText(progress);

        return view;
    }
}
