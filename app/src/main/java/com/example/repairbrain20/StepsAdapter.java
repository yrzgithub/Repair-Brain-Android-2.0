package com.example.repairbrain20;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

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
    static boolean delete = false;
    DatabaseReference delete_reference;
    Snackbar snack;

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

    StepsAdapter(Activity act,View view, Map<String, Step> map,boolean delete)
    {
        this(act,view,map);
        StepsAdapter.delete = delete;

        try
        {
            snack = Snackbar.make(view,"Reload the list", BaseTransientBottomBar.LENGTH_INDEFINITE);
        }
        catch (Exception e)
        {

        }

        delete_reference = User.getRepairReference();
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
        ImageView delete = view.findViewById(R.id.delete);

        if(StepsAdapter.delete)
        {
            image.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        }
        else
        {
            image.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
        }

        String key = keys.get(i);

        Step step_ = steps.get(key);

        String source_link = step_.getLink().trim();
        String source_name = step_.getSource_name().trim();

        if(source_name.equals(""))
        {
            source_name = "Not found";
        }

        source_name = source_name.substring(0,1).toUpperCase() + source_name.substring(1);

        step.setText(key.trim());
        source.setText(source_name);

        if(StepsAdapter.delete)
        {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(delete_reference!=null)
                    {
                        if(snack!=null) snack.show();
                        delete_reference.child("next_steps").child(key).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if(snack!=null) snack.dismiss();
                            }
                        });
                    }
                }
            });
        }

        final String source_copy = source_link;

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(source_copy.equals(""))
                {
                    Toast.makeText(activity,"Source link not found",Toast.LENGTH_SHORT).show();
                    return;
                }

                try
                {
                    String link = source_copy;

                    if(!link.startsWith("http"))
                    {
                        link = "https://" + source_copy;
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
