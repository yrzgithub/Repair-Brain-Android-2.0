package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterPosNeg extends BaseAdapter {

    static boolean remove = false;
    Activity activity;
    Map<String, String> map = new HashMap<>();
    List<String> keys = new ArrayList<>();
    boolean delete = false;
    String effect;
    ViewPager pager;
    ListView list;
    ImageView img;
    View view;
    Snackbar snack;
    RelativeLayout main;

    AdapterPosNeg(Activity act, View view, Map<String, String> map, String effect) {
        this.activity = act;
        this.pager = act.findViewById(R.id.view_pager);

        this.view = view;

        this.list = view.findViewById(R.id.list);
        this.img = view.findViewById(R.id.no_results);
        this.main = view.findViewById(R.id.main);

        this.effect = effect;

        try {
            snack = Snackbar.make(main, "Reload the list", BaseTransientBottomBar.LENGTH_INDEFINITE);
            snack.setAction("Reload", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    act.recreate();
                }
            });
        } catch (Exception e) {

        }

        if (map != null) {
            this.map.putAll(map);
        }

        if (this.map.isEmpty() || map == null) {
            show_image_view(R.drawable.noresultfound);
        } else {
            keys.addAll(this.map.keySet());
            img.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
    }

    AdapterPosNeg(Activity act, View view, Map<String, String> map, String type, boolean delete) {
        this(act, view, map, type);

        if (this.map.isEmpty() || map == null) this.delete = remove = false;
        else this.delete = remove = delete;


        if (snack != null && this.map.size() > 0 && delete) {
            snack.show();
        }

        if (this.map.isEmpty()) {
            remove = false;
        }
    }

    public void show_image_view(int id) {
        img.setVisibility(View.VISIBLE);
        img.setForegroundGravity(Gravity.CENTER);
        list.setVisibility(View.GONE);
        if (activity != null && !activity.isDestroyed()) Glide.with(img).load(id).into(img);
    }

    @Override
    public int getCount() {
        //Log.e("sanjay_map",String.valueOf(map.size()));
        return map.size();
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

        view = activity.getLayoutInflater().inflate(R.layout.custom_list_effects, null, false);

        TextView text_widget = view.findViewById(R.id.effect);
        TextView date_widget = view.findViewById(R.id.date);

        text_widget.post(new Runnable() {
            @Override
            public void run() {
                text_widget.setSelected(true);
            }
        });

        ImageView icon = view.findViewById(R.id.image);
        ImageView delete = view.findViewById(R.id.delete);

        if (this.effect.equals("positive_effects")) {
            icon.setImageResource(R.drawable.positive_effect);
        } else if (this.effect.equals("negative_effects")) {
            icon.setImageResource(R.drawable.negative_effects);
        } else {
            icon.setImageResource(R.drawable.steps);
        }

        String key = keys.get(i);

        if (this.delete) {
            delete.setVisibility(View.VISIBLE);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference reference = User.getRepairReference();

                    if (reference != null) {
                        reference.child(effect).child(key).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (snack != null) snack.dismiss();
                            }
                        });
                    }

                }
            });
        } else {
            delete.setVisibility(View.GONE);
        }

        String show = key.substring(0, 1).toUpperCase() + key.substring(1);
        text_widget.setText(show);
        date_widget.setText(map.get(key));

        return view;
    }
}

