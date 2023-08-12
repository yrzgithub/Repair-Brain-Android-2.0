package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CommonPosNegNextAdapter extends BaseAdapter {

    Activity act;
    List<String> list;

    CommonPosNegNextAdapter(Activity act,List<String> list)
    {
        this.act = act;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list!=null?list.size():0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(act).inflate(R.layout.common_effects_and_habits_list,null);
        TextView effect = view.findViewById(R.id.effect);

        effect.setText(list.get(i));

        return view;
    }
}
