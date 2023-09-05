package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterCommonPosNegNext extends BaseAdapter {

    Activity act;
    List<String> keys = new ArrayList<>();
    Map<String, Common> map = new HashMap<>();
    boolean add = false;
    String today, type;
    DatabaseReference reference;
    List<String> present;
    Snackbar bar;
    ListView list;
    Map<String, Drawable> icons = new HashMap<>();
    boolean next_steps = false;
    StorageReference store = FirebaseStorage.getInstance().getReference().child("icons");

    AdapterCommonPosNegNext(Activity act, Map<String, Common> map) {
        this.act = act;

        ImageView loading = act.findViewById(R.id.loading);
        list = act.findViewById(R.id.effects);

        if (map != null) {
            this.map.putAll(map);
            keys.addAll(map.keySet());
        } else {
            list.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            Glide.with(loading).load(R.drawable.noresultfound).into(loading);
        }
    }

    AdapterCommonPosNegNext(Activity act, Map<String, Common> map, ArrayList<String> present, String type, boolean add) {
        this(act, map);
        this.add = add;
        this.type = type.replace("common_", "");
        this.present = present;

        if (this.type.equals("next_steps")) {
            next_steps = true;
        }

        if (this.present == null) this.present = new ArrayList<>();

        bar = Snackbar.make(list, "Adding", BaseTransientBottomBar.LENGTH_INDEFINITE);

        LocalDateTime local_date_time = LocalDateTime.now();
        today = DateTimeFormatter.ofPattern("E, MMM dd yyyy").format(local_date_time);
    }

    @Override
    public int getCount() {
        return keys != null ? keys.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return keys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public Common getCommon(int i) {
        return map.get(keys.get(i));
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(act).inflate(R.layout.common_effects_and_habits_list, null);

        TextView effect = view.findViewById(R.id.effect);
        TextView source = view.findViewById(R.id.source);
        ImageView go = view.findViewById(R.id.go);

        ImageView image = view.findViewById(R.id.image);

        String key = keys.get(i);

        if (this.add) {
            if (present.contains(key)) {
                go.setImageResource(R.drawable.tick);
            } else {
                go.setImageResource(R.drawable.add);
            }
        }

        effect.setSelected(true);
        effect.setText(key);

        Common common = map.get(key);
        String source_name = common.getSource();
        String link = common.getLink();

        String file_name = source_name.toLowerCase() + ".png";

        if(!act.isDestroyed())
        {
            if(icons.containsKey(source_name))
            {
                image.setImageDrawable(icons.get(source_name));
            }
            else
            {
                if(store!=null)
                {
                    store.child(file_name)
                            .getDownloadUrl()
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful())
                                    {
                                        Uri uri = task.getResult();
                                        Glide.with(act)
                                                .asDrawable()
                                                .load(uri)
                                                .into(new SimpleTarget<Drawable>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                        icons.put(source_name,resource);
                                                        image.setImageDrawable(resource);
                                                    }
                                                });
                                    }
                                    else
                                    {
                                        Log.e("glide_error",task.getException().getMessage());
                                    }
                                }
                            });
                }
            }
        }

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AdapterCommonPosNegNext.this.add) {
                    reference = User.getRepairReference();

                    if (reference != null) {
                        String key_trimmed = key.trim();
                        if (!present.contains(key_trimmed)) {
                            bar.show();
                            reference.child(type).child(key_trimmed).setValue(next_steps ? new Step(source_name, link) : today)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            bar.dismiss();
                                            go.setImageResource(R.drawable.tick);
                                            present.add(key_trimmed);
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(act, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
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
