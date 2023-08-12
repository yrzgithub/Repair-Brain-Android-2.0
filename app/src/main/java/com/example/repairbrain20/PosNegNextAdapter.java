package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PosNegNextAdapter extends BaseAdapter {

    Activity activity;
    Map<String,String> map;
    List<String> keys;
    boolean delete = false;
    String effect;
    ViewPager pager;
    ListView list;
    ImageView img;
    View view;

    PosNegNextAdapter(Activity act,View view,Map<String,String> map,String effect)
    {
        this.activity = act;
        this.pager = act.findViewById(R.id.view_pager);

        this.view = view;

        this.list = view.findViewById(R.id.list);
        this.img = view.findViewById(R.id.no_results);

        this.map = map;
        this.effect = effect;

        if(map==null || map.size()==0) show_image_view(R.drawable.noresultfound);

        else
        {
            keys =  new ArrayList<>(map.keySet());
            img.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
    }

    public void show_image_view(int id)
    {
        img.setVisibility(View.VISIBLE);
        img.setForegroundGravity(Gravity.CENTER);
        list.setVisibility(View.GONE);
        Glide.with(img).load(id).into(img);
    }

    PosNegNextAdapter(Activity act,View view,Map<String,String> map,String type,boolean delete)
    {
        this(act,view,map,type);
        this.delete = delete;
    }

    @Override
    public int getCount() {
        //Log.e("sanjay_map",String.valueOf(map.size()));
        return map!=null?map.size():0;
    }

    @Override
    public Object getItem(int i) {
        return keys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.custom_list_effects,null,false);

        TextView text_widget = view.findViewById(R.id.effect);
        TextView date_widget = view.findViewById(R.id.date);
        ImageView delete = view.findViewById(R.id.delete);

        String key = keys.get(i);

        if(this.delete)
        {
            delete.setVisibility(View.VISIBLE);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("uruttu_click",String.valueOf(i));

                    DatabaseReference reference = User.getReference();

                    if(reference!=null)
                    {
                         Snackbar bar = Snackbar.make(pager,"Removing", BaseTransientBottomBar.LENGTH_INDEFINITE);
                         bar.show();
                        reference.child(effect).child(key).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                bar.dismiss();
                                Toast.makeText(activity,"Effect Removed",Toast.LENGTH_LONG).show();
                                map.remove(key);
                                Log.e("map_uruttu",map.toString());
                                list.setAdapter(new PosNegNextAdapter(activity,PosNegNextAdapter.this.view,map,effect,PosNegNextAdapter.this.delete));
                            }
                        });
                    }

                }
            });
        }
        else
        {
            delete.setVisibility(View.GONE);
        }

        text_widget.setSelected(true);

        text_widget.setText(key);
        date_widget.setText(map.get(key));

        return view;
    }
}

