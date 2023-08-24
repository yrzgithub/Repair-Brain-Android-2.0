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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterTriggers extends BaseAdapter {
    Activity act;
    Map<String,String> triggers = new HashMap<>();
    List<String> keys = new ArrayList<>();
    static boolean delete = false;
    View view;
    ListView list;
    Snackbar bar;
    LinearLayout main;

    AdapterTriggers(Activity activity,View view, Map<String,String> map)
    {
        this.act = activity;
        this.view = view;
        this.main = view.findViewById(R.id.main);

        list = view.findViewById(R.id.list);
        ImageView loading = view.findViewById(R.id.loading);

        try
        {
            bar = Snackbar.make(main,"Reload the list",BaseTransientBottomBar.LENGTH_INDEFINITE);
            bar.setAction("Reload", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    act.recreate();
                }
            });
        }
        catch (Exception e)
        {

        }

        if(map.size()==0)
        {
            list.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            if(activity!=null && !activity.isDestroyed()) Glide.with(loading).load(R.drawable.noresultfound).into(loading);
            delete = false;
        }
        else
        {
            list.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            triggers.putAll(map);
            keys.addAll(map.keySet());
        }
    }

    AdapterTriggers(Activity activity,View view,Map<String,String> trigger,boolean delete)
    {
        this(activity,view,trigger);
        AdapterTriggers.delete = delete;

        if(bar!=null && this.triggers.size()>0 && delete)
        {
            bar.show();
        }
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
        ImageView delete = view.findViewById(R.id.delete);

        if(AdapterTriggers.delete)
        {
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String key = trigger_name.getText().toString();

                    DatabaseReference reference = User.getRepairReference();
                    reference
                            .child("triggers/"+User.selected_addiction)
                            .child(key)
                            .removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                }
                            });
                }
            });
        }

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        String trigger = keys.get(i);
        String added_on = triggers.get(trigger);

        trigger_name.setText(trigger);
        date_added.setText(added_on);

        return view;
    }
}