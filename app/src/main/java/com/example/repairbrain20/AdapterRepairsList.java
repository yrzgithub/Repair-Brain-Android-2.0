package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterRepairsList extends BaseAdapter {

    static boolean delete = false;
    Map<String, Repairs> addictions = new HashMap<>();
    List<String> keys = new ArrayList<>();
    Activity act;
    ListView list;
    ImageView no_results;
    Snackbar snack;

    AdapterRepairsList(Activity act, View view, Map<String, Repairs> addictions) {
        this.act = act;

        this.list = view.findViewById(R.id.list);
        no_results = view.findViewById(R.id.no_results);

        if (addictions != null && addictions.size() > 0) {
            list.setVisibility(View.VISIBLE);
            no_results.setVisibility(View.GONE);
            this.addictions.putAll(addictions);
            this.keys.addAll(addictions.keySet());
        } else {
            list.setVisibility(View.GONE);
            no_results.setVisibility(View.VISIBLE);
            if (act != null && !act.isDestroyed())
                Glide.with(no_results).load(R.drawable.noresultfound).into(no_results);
        }

    }

    AdapterRepairsList(Activity act, View view, Map<String, Repairs> addictions, boolean delete) {
        this(act, view, addictions);

        if (addictions != null && addictions.size() > 0) {
            AdapterRepairsList.delete = delete;
        } else {
            AdapterRepairsList.delete = false;
        }

        try {
            snack = Snackbar.make(list, "Reload the list", BaseTransientBottomBar.LENGTH_INDEFINITE);
            snack.setAction("Reload", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    act.recreate();
                }
            });
        } catch (Exception ignored) {

        }

        if (AdapterRepairsList.delete && snack != null && this.addictions.size() > 0) {
            snack.show();
        }
    }

    @Override
    public int getCount() {
        return addictions != null ? addictions.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return addictions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view!=null) return view;

        view = act.getLayoutInflater().inflate(R.layout.custom_repair_list_view, null);

        RelativeLayout main = view.findViewById(R.id.main);
        TextView addiction_name_view = view.findViewById(R.id.addiction_name);
        TextView date_added_view = view.findViewById(R.id.date_added);
        ImageView delete_or_go = view.findViewById(R.id.delete_or_go);

        addiction_name_view.post(new Runnable() {
            @Override
            public void run() {
                addiction_name_view.setSelected(true);
            }
        });

        String key = keys.get(i);

        Repairs addiction = this.addictions.get(key);

        DatabaseReference reference = User.getMainReference();

        String date_added = addiction.getDate_added();

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.setAddiction(act, key);
                act.startActivity(new Intent(act, ActHome.class));
            }
        });

        if (delete) {
            delete_or_go.setImageResource(R.drawable.delete_icon);
            main.setEnabled(false);
        }

        delete_or_go.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!delete) {
                    add_note(reference, key);
                }
                return true;
            }
        });

        delete_or_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (delete) {
                    if (reference != null) {
                        reference.child(key).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (snack != null) snack.dismiss();
                            }
                        });
                    }
                } else {
                    if (reference != null) {
                        reference
                                .child(key)
                                .child("note")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            String link = task.getResult().getValue(String.class);

                                            if (link == null) {
                                                Toast.makeText(act, "Note link not found", Toast.LENGTH_SHORT).show();
                                                add_note(reference, key);
                                            } else {
                                                browse(link);
                                            }
                                        }
                                    }
                                });
                    }
                }

            }
        });

        addiction_name_view.setText(key);
        date_added_view.setText(date_added);

        return view;
    }

    public void browse(String link) {
        try {
            if (!link.startsWith("http")) {
                link = "https://" + link;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            act.startActivity(intent);
        } catch (Exception | Error e) {
            Toast.makeText(act, "Invalid note link", Toast.LENGTH_SHORT).show();
        }
    }

    public void add_note(DatabaseReference reference, String key) {
        View view_ = act.getLayoutInflater().inflate(R.layout.alert_note, null);
        EditText link_ = view_.findViewById(R.id.effects_list);

        new AlertDialog.Builder(act)
                .setView(view_)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String link = link_.getText().toString();

                        if (!FragmentSteps.isValidLink(link)) {
                            Toast.makeText(act, "Invalid link", Toast.LENGTH_LONG).show();
                            return;
                        }

                        reference.child(key).child("note").setValue(link);
                        browse(link);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

}
