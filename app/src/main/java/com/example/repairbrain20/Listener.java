package com.example.repairbrain20;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Listener {

    ListView list;
    DatabaseReference list_effects_reference;
    Map<String, String> map;
    String type;
    Activity act;
    View view;
    ImageView loading;
    DatabaseReference common_reference = FirebaseDatabase.getInstance().getReference();

    Listener(Activity act, View view, String type) {
        this.act = act;
        this.view = view;
        this.loading = view.findViewById(R.id.no_results);

        this.list = view.findViewById(R.id.list);

        this.type = type;

        list.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        Glide.with(loading)
                .load(R.drawable.loading_pink_list)
                .into(loading);

        list_effects_reference = User.getRepairReference();

        if (list_effects_reference != null) {
            list_effects_reference.child(type).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    map = snapshot.getValue(new GenericTypeIndicator<Map<String, String>>() {
                        @NonNull
                        @Override
                        public String toString() {
                            return super.toString();
                        }
                    });

                    if (AdapterPosNeg.remove)
                        list.setAdapter(new AdapterPosNeg(act, view, map, type, true));
                    else list.setAdapter(new AdapterPosNeg(act, view, map, type));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public Map<String, String> getEffectsMap() {
        return map;
    }

    public void addEffect() {
        View view = View.inflate(act, R.layout.alert_dialog, null);
        AutoCompleteTextView search = view.findViewById(R.id.effects_list);
        search.setThreshold(0);

        search.setHint("Search or Enter");

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                search.showDropDown();
            }
        });

        Log.e("common", "common_" + type);

        if (common_reference != null) {
            common_reference
                    .child("common_" + type)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            Map<String, Common> map = task.getResult().getValue(new GenericTypeIndicator<Map<String, Common>>() {
                                @NonNull
                                @Override
                                public String toString() {
                                    return super.toString();
                                }
                            });

                            if (map != null) {
                                List<String> suggestions = new ArrayList<>(map.keySet());

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(act, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, suggestions);
                                search.setAdapter(adapter);
                            }
                        }
                    });
        }

        new AlertDialog.Builder(act)
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String effect_new = search.getText().toString().trim();

                        if (effect_new.trim().isEmpty()) {
                            Toast.makeText(act.getApplicationContext(), "Invalid " + type.replace("_", " ").substring(0, type.length() - 1), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        LocalDateTime date_time = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

                        String date_added = date_time.format(formatter);

                        DatabaseReference reference = User.getRepairReference();

                        if (!Data.isValidKey(effect_new)) {
                            Toast.makeText(act, "Invalid effect name", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (reference != null) {
                            Snackbar bar = Snackbar.make(list, "Adding", BaseTransientBottomBar.LENGTH_INDEFINITE);
                            bar.show();

                            reference.child(type)
                                    .child(effect_new)
                                    .setValue(date_added)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            bar.dismiss();
                                            reference.child("lastly_noted_" + type).setValue(effect_new);

                                            Toast.makeText(act, "Successfully Added", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void reset() {
        DatabaseReference reference = User.getRepairReference();

        if (reference != null) {
            Snackbar bar = Snackbar.make(list, "Resetting", BaseTransientBottomBar.LENGTH_INDEFINITE);
            bar.show();
            reference.child(type).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    bar.dismiss();
                    Toast.makeText(act, "Successfully Resetted", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
