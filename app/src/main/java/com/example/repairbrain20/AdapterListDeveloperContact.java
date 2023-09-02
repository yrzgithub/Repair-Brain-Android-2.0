package com.example.repairbrain20;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterListDeveloperContact extends BaseAdapter {

    Activity activity;
    Map<String,Contact> contact = new HashMap<>();
    List<String> keys = new ArrayList<>();
    StorageReference storage = FirebaseStorage.getInstance().getReference().child("social/");
    Map<String,Drawable> icons = new HashMap<>();

    AdapterListDeveloperContact(Activity act, Map<String,Contact> map)
    {
        activity = act;

        ListView list = act.findViewById(R.id.list);
        ImageView loading = act.findViewById(R.id.loading);

        if(map!=null)
        {
            contact.putAll(map);
            keys.addAll(map.keySet());
        }

        if(contact.size()==0)
        {
            list.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            Glide.with(activity).load(R.drawable.noresultfound).into(loading);
        }
        else
        {
            list.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }
    }

    @Override
    public int getCount() {
        return contact.size();
    }

    @Override
    public Object getItem(int i) {
        return keys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = activity.getLayoutInflater().inflate(R.layout.custom_list_developer,null);

        ImageView image = view.findViewById(R.id.image);
        ImageView go = view.findViewById(R.id.go);

        TextView name = view.findViewById(R.id.name);
        TextView username = view.findViewById(R.id.username);

        String key_name = keys.get(i);

        Contact contact_ = contact.get(key_name);

        String username_ = contact_.getUsername();
        String link = contact_.getLink();

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                activity.startActivity(intent);
            }
        });

        if (icons.containsKey(key_name))
        {
            image.setImageDrawable(icons.get(key_name));
        }
        else
        {
            if(storage!=null)
            {
                storage.child(key_name.toLowerCase()+".png")
                        .getDownloadUrl()
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful())
                                {
                                    if(!activity.isDestroyed())
                                    {
                                        Glide.with(activity).asDrawable().load(task.getResult())
                                                .into(new SimpleTarget<Drawable>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                        icons.put(key_name,resource);
                                                        image.setImageDrawable(resource);
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        }

        name.setText(key_name);
        username.setText(username_);

        return view;
    }
}
