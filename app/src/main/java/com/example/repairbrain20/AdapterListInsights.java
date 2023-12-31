package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterListInsights extends BaseAdapter {

    static boolean remove = false;
    Map<String, Insight> insights = new HashMap<>();
    Activity activity;
    View view;
    Snackbar snack;
    List<String> keys = new ArrayList<>();
    StorageReference reference = FirebaseStorage.getInstance().getReference();
    DatabaseReference common_reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference delete_reference;
    Map<String, Drawable> drawables = new HashMap<>();

    AdapterListInsights(Activity act, View view_, Map<String, Insight> map) {
        this.activity = act;
        this.view = view_;

        ListView list = view.findViewById(R.id.list);
        ImageView no_results = view.findViewById(R.id.no_results);

        if (map != null) {
            this.insights.putAll(map);
            this.keys.addAll(map.keySet());
        }

        if (map == null || insights.isEmpty()) {
            list.setVisibility(View.GONE);
            no_results.setVisibility(View.VISIBLE);
            if (act != null && !act.isDestroyed())
                Glide.with(no_results).load(R.drawable.noresultfound).into(no_results);

        } else {
            list.setVisibility(View.VISIBLE);
            no_results.setVisibility(View.GONE);
        }
    }

    AdapterListInsights(Activity act, View view_, Map<String, Insight> map, boolean delete) {
        this(act, view_, map);

        if (map == null || insights.isEmpty()) AdapterListInsights.remove = false;
        else AdapterListInsights.remove = delete;

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

        if (remove && snack != null && this.insights.size() > 0) {
            snack.show();
        }

        delete_reference = User.getInsightsReference();
    }

    @Override
    public int getCount() {
        return insights.size();
    }

    @Override
    public Object getItem(int i) {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = activity.getLayoutInflater().inflate(R.layout.custom_insights, null);

        ImageView start = view.findViewById(R.id.image);
        ImageView go = view.findViewById(R.id.go);

        TextView insight = view.findViewById(R.id.insight);

        TextView source = view.findViewById(R.id.source);
        ImageView delete = view.findViewById(R.id.delete);

        if (AdapterListInsights.remove) {
            go.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        } else {
            go.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
        }

        String key_insight = keys.get(i);

        Insight insight_ = insights.get(key_insight);

        if (!remove) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view_) {

                    View view = activity.getLayoutInflater().inflate(R.layout.custom_edit_insights, null);

                    AutoCompleteTextView source = view.findViewById(R.id.source_name);
                    EditText link = view.findViewById(R.id.link);

                    source.setThreshold(0);

                    source.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            source.showDropDown();
                        }
                    });

                    if (common_reference != null) {
                        common_reference
                                .child("common_steps_sources")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            List<String> keys = task.getResult().getValue(new GenericTypeIndicator<List<String>>() {
                                                @NonNull
                                                @Override
                                                public String toString() {
                                                    return super.toString();
                                                }
                                            });

                                            if (keys != null) {
                                                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, keys);
                                                source.setAdapter(adapter);
                                            }
                                        }
                                    }
                                });
                    }

                    new AlertDialog.Builder(activity)
                            .setView(view)
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String source_ = source.getText().toString();
                                    String link_ = link.getText().toString();

                                    if (key_insight.isEmpty()) {
                                        Toast.makeText(activity, "Insight cannot be empty", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (!source_.isEmpty() && link_.isEmpty()) {
                                        Toast.makeText(activity, "Please paste the link", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (!link_.isEmpty() && !FragmentSteps.isValidLink(link_)) {
                                        Toast.makeText(activity, "Invalid source link", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    if (!Data.isValidKey(key_insight)) {
                                        Toast.makeText(activity, "Invalid Insight name", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    DatabaseReference reference = User.getInsightsReference();

                                    if (reference != null) {

                                        Snackbar snack = Snackbar.make(AdapterListInsights.this.view, "Adding", BaseTransientBottomBar.LENGTH_INDEFINITE);
                                        snack.show();

                                        reference
                                                .child(key_insight)
                                                .setValue(new Insight(source_, link_))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        snack.dismiss();
                                                    }
                                                });
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                    return true;
                }
            });
        }

        String source_name = insight_.getSource();
        String link = insight_.getLink();

        if (source_name.isEmpty()) {
            source_name = "Not found";
        }

        source_name = source_name.substring(0, 1).toUpperCase() + source_name.substring(1);

        if (AdapterListInsights.remove) {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (delete_reference != null) {
                        if (snack != null) snack.show();
                        delete_reference.child(key_insight).removeValue(new DatabaseReference.CompletionListener() {
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

        final String source_link_copy = link;

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (source_link_copy.isEmpty()) {
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

        insight.post(new Runnable() {
            @Override
            public void run() {
                insight.setSelected(true);
            }
        });
        insight.setText(key_insight.trim());
        source.setText(source_name.trim());

        return view;
    }
}
