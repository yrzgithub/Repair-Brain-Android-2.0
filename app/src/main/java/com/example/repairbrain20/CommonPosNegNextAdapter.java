package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonPosNegNextAdapter extends BaseAdapter {

    Activity act;
    List<String> keys;
    Map<String,Common> map;

    CommonPosNegNextAdapter(Activity act, Map<String,Common> map)
    {
        this.act = act;
        this.map = map;

        keys = new ArrayList<>(map.keySet());
    }

    @Override
    public int getCount() {
        return keys!=null?keys.size():0;
    }

    @Override
    public Object getItem(int i) {
        return keys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public Common getCommon(int i)
    {
        return map.get(keys.get(i));
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(act).inflate(R.layout.common_effects_and_habits_list,null);

        TextView effect = view.findViewById(R.id.effect);
        TextView source = view.findViewById(R.id.source);
        ImageView go = view.findViewById(R.id.go);

        String key = keys.get(i).trim();
        String title =  key.substring(0,1).toUpperCase() + key.substring(1);

        effect.setSelected(true);
        effect.setText(title);

        Common common = map.get(key);
        String source_name = common.getSource();
        String link = common.getLink();

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                act.startActivity(intent);
            }
        });

        source.setText(source_name);

        return view;
    }
}