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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterCommonPosNegNext extends BaseAdapter {

    Activity act;
    List<String> keys = new ArrayList<>();
    Map<String,Common> map = new HashMap<>();
    boolean add = false;
    String today,type;
    DatabaseReference reference;
    List<String> present;

    AdapterCommonPosNegNext(Activity act, Map<String,Common> map)
    {
        this.act = act;

        ImageView loading = act.findViewById(R.id.loading);
        ListView list = act.findViewById(R.id.effects);

        if(map!=null)
        {
            this.map.putAll(map);
            keys.addAll(map.keySet());
        }
        else
        {
            list.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            Glide.with(loading).load(R.drawable.noresultfound).into(loading);
        }
    }

    AdapterCommonPosNegNext(Activity act,Map<String,Common> map,ArrayList<String> present,String type,boolean add)
    {
        this(act,map);
        this.add = add;
        this.type = type.replace("common_","");
        this.present = present;

        LocalDateTime local_date_time = LocalDateTime.now();
        today = DateTimeFormatter.ofPattern("E, MMM dd yyyy").format(local_date_time);
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

        if(this.add)
        {
            if(present.contains(key))
            {
                go.setImageResource(R.drawable.tick);
            }
            else
            {
                go.setImageResource(R.drawable.add);
            }
        }

        effect.setSelected(true);
        effect.setText(title);

        Common common = map.get(key);
        String source_name = common.getSource();
        String link = common.getLink();

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AdapterCommonPosNegNext.this.add)
                {
                    reference = User.getRepairReference();

                    if(reference!=null)
                    {
                        if(!present.contains(key))
                        {
                            reference.child(type).child(key).setValue(today)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            go.setImageResource(R.drawable.tick);
                                            present.add(key);
                                        }
                                    });
                        }
                    }
                    else
                    {
                        Toast.makeText(act,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(link));
                    act.startActivity(intent);
                }
            }
        });

        source.setText(source_name);

        return view;
    }
}
