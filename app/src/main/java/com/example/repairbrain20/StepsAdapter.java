package com.example.repairbrain20;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepsAdapter extends BaseAdapter {

    Map<String,Step> steps = new HashMap<>();
    Activity activity;
    ImageView no_results;
    ListView list;
    List<String> keys = new ArrayList<>();

    StepsAdapter(Activity act,View view, Map<String, Step> map)
    {
        activity = act;

        list = view.findViewById(R.id.list);
        no_results = view.findViewById(R.id.no_results);

        if(map!=null && map.size()>0)
        {
            list.setVisibility(View.VISIBLE);
            no_results.setVisibility(View.GONE);
            steps.putAll(map);
            keys.addAll(map.keySet());
        }
        else
        {
            list.setVisibility(View.GONE);
            no_results.setVisibility(View.VISIBLE);
            Glide.with(activity).load(R.drawable.noresultfound).into(no_results);
        }
    }

    @Override
    public int getCount() {
        return steps.size();
    }

    @Override
    public Object getItem(int i) {
        return steps.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = activity.getLayoutInflater().inflate(R.layout.custom_steps,null);

        TextView step = view.findViewById(R.id.step);
        TextView source = view.findViewById(R.id.source);

        ImageView image = view.findViewById(R.id.go);

        String key = keys.get(i);

        Step step_ = steps.get(key);

        String source_link = step_.getLink().trim();
        String source_name = step_.getSource_name().trim();

        source_name = source_name.substring(0,1).toUpperCase() + source_name.substring(1);

        step.setText(key.trim());
        source.setText(source_name);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try
                {
                    String link = source_link;

                    if(!link.startsWith("http"))
                    {
                        link = "https://" + source_link;
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(link));
                    activity.startActivity(intent);
                }
                catch (Exception | Error e)
                {
                    Toast.makeText(activity,"Invalid source link",Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }
}
