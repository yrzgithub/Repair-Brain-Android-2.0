package com.example.repairbrain20;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterSteps extends BaseAdapter {

    static boolean delete = false;
    Map<String, Step> steps = new HashMap<>();
    Activity activity;
    ImageView no_results;
    ListView list;
    List<String> keys = new ArrayList<>();
    DatabaseReference delete_reference;
    Snackbar snack;
    StorageReference reference = FirebaseStorage.getInstance().getReference();
    Map<String, Drawable> drawables = new HashMap<>();

    AdapterSteps(Activity act, View view, Map<String, Step> map) {
        activity = act;

        list = view.findViewById(R.id.list);
        no_results = view.findViewById(R.id.no_results);

        if (map != null && map.size() > 0) {
            list.setVisibility(View.VISIBLE);
            no_results.setVisibility(View.GONE);
            steps.putAll(map);
            keys.addAll(map.keySet());
        } else {
            list.setVisibility(View.GONE);
            no_results.setVisibility(View.VISIBLE);

            if (act != null && !act.isDestroyed())
                Glide.with(no_results).load(R.drawable.noresultfound).into(no_results);
        }
    }

    AdapterSteps(Activity act, View view, Map<String, Step> map, boolean delete) {
        this(act, view, map);

        if (map != null && map.size() > 0) AdapterSteps.delete = delete;
        else AdapterSteps.delete = false;

        try {
            snack = Snackbar.make(view, "Reload the list", BaseTransientBottomBar.LENGTH_INDEFINITE);
            snack.setAction("reload", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    act.recreate();
                }
            });
        } catch (Exception e) {

        }

        if (delete && snack != null && this.steps.size() > 0) {
            snack.show();
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

        view = activity.getLayoutInflater().inflate(R.layout.custom_steps, null);

        TextView step = view.findViewById(R.id.step);
        TextView source = view.findViewById(R.id.source);

        ImageView image = view.findViewById(R.id.go);
        ImageView delete = view.findViewById(R.id.delete);
        ImageView start = view.findViewById(R.id.image);

        if (AdapterSteps.delete) {
            image.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
        }

        String key = keys.get(i);

        Step step_ = steps.get(key);

        String source_link = step_.getLink().trim();
        String source_name = step_.getSource_name().trim();

        if (source_name.equals("")) {
            source_name = "Not found";
        }

        source_name = source_name.substring(0, 1).toUpperCase() + source_name.substring(1);

        step.setText(key.trim());
        source.setText(source_name);

        if (AdapterSteps.delete) {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (delete_reference != null) {
                        if (snack != null) snack.show();
                        delete_reference.child("next_steps").child(key).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (snack != null) snack.dismiss();
                            }
                        });
                    }
                }
            });
        } else {
            StorageReference download_reference = reference.child("icons/").child(source_name.toLowerCase() + ".png");

            if (drawables.containsKey(source_name)) {
                start.setImageDrawable(drawables.get(source_name));
            } else {
                final String final_source_name = source_name;

                try {
                    download_reference.getDownloadUrl()
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        if (!activity.isDestroyed()) {
                                            Glide.with(activity).asDrawable().load(task.getResult()).into(new SimpleTarget<Drawable>() {
                                                @Override
                                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                    drawables.put(final_source_name, resource);
                                                    start.setImageDrawable(resource);
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                } catch (Exception e) {
                    Log.e("image", "image not found");
                }
            }
        }

        final String source_link_copy = source_link;

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (source_link_copy.equals("")) {
                    Toast.makeText(activity, "Source link not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String link = source_link_copy;

                    if (!link.startsWith("http")) {
                        link = "https://" + source_link_copy;
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(link));
                    activity.startActivity(intent);
                } catch (Exception | Error e) {
                    Toast.makeText(activity, "Invalid source link", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
