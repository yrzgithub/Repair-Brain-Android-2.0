package com.example.repairbrain20;

import static com.example.repairbrain20.R.drawable;
import static com.example.repairbrain20.R.id;
import static com.example.repairbrain20.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentTriggers extends Fragment {

    ListView list;
    ImageView loading;
    View view;
    Map<String, String> map;
    Activity activity;

    public FragmentTriggers() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.activity = getActivity();
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        AdapterTriggers.delete = false;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(layout.fragment_triggers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        this.view = view;

        list = view.findViewById(id.list);
        loading = view.findViewById(id.loading);

        Glide.with(FragmentTriggers.this.activity).load(drawable.loading_pink_list).into(loading);

        DatabaseReference reference = User.getRepairReference();

        if (reference != null) {
            reference.child("triggers/" + User.selected_addiction)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            map = snapshot.getValue(new GenericTypeIndicator<Map<String, String>>() {
                                @NonNull
                                @Override
                                public String toString() {
                                    return super.toString();
                                }
                            });

                            if (AdapterTriggers.delete)
                                list.setAdapter(new AdapterTriggers(FragmentTriggers.this.activity, FragmentTriggers.this.view, map, true));
                            else
                                list.setAdapter(new AdapterTriggers(FragmentTriggers.this.activity, FragmentTriggers.this.view, map));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.triggers_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        DatabaseReference reference;
        Intent intent;

        switch (item.getItemId()) {
            case R.id.add:
                View view = getLayoutInflater().inflate(R.layout.alert_dialog, null);
                AutoCompleteTextView triggers_view = view.findViewById(id.effects_list);

                triggers_view.setHint("Search or Enter");
                triggers_view.setThreshold(0);

                triggers_view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        triggers_view.showDropDown();
                    }
                });

                DatabaseReference main_reference = FirebaseDatabase.getInstance().getReference();
                main_reference
                        .child("common_triggers")
                        .child(User.selected_addiction)
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
                                    List<String> list = new ArrayList<>(map.keySet());
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(FragmentTriggers.this.activity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list);
                                    triggers_view.setAdapter(adapter);
                                }
                            }
                        });

                reference = User.getRepairReference();

                if (reference != null) {
                    new AlertDialog.Builder(FragmentTriggers.this.activity)
                            .setView(view)
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String trigger_name = triggers_view.getText().toString().trim();

                                    LocalDateTime local_time = LocalDateTime.now();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

                                    String time_ = local_time.format(formatter);

                                    if (!Data.isValidKey(trigger_name)) {
                                        Toast.makeText(FragmentTriggers.this.activity, "Invalid trigger name", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    Snackbar snack = Snackbar.make(FragmentTriggers.this.view, "Adding", BaseTransientBottomBar.LENGTH_INDEFINITE);
                                    snack.show();

                                    reference
                                            .child("triggers/" + User.selected_addiction)
                                            .child(trigger_name)
                                            .setValue(time_)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    snack.dismiss();
                                                    Toast.makeText(FragmentTriggers.this.activity, "Trigger added", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnCanceledListener(new OnCanceledListener() {
                                                @Override
                                                public void onCanceled() {
                                                    //snack.dismiss();
                                                }
                                            });
                                }
                            })
                            .create()
                            .show();
                }
                break;

            case id.remove:
                if (map == null || map.isEmpty()) {
                    Toast.makeText(activity, "Triggers list is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    list.setAdapter(new AdapterTriggers(FragmentTriggers.this.activity, FragmentTriggers.this.view, map, true));
                }
                break;

            case id.add_common:
                AdapterTriggers.delete = false;

                ArrayList<String> present;

                if (map != null) present = new ArrayList<>(map.keySet());
                else present = new ArrayList<>();

                intent = new Intent(FragmentTriggers.this.activity, ActCommon.class);
                intent.putExtra("common", "common_triggers/" + User.selected_addiction);
                intent.putExtra("add", true);
                intent.putExtra("present", (Serializable) present);
                startActivity(intent);
                break;

            case id.common:
                intent = new Intent(FragmentTriggers.this.activity, ActCommon.class);
                intent.putExtra("common", "common_triggers/" + User.selected_addiction);
                intent.putExtra("add", false);
                intent.putExtra("present", (Serializable) null);
                startActivity(intent);
                break;

            case id.reset:
                reference = User.getRepairReference();
                Snackbar snack = Snackbar.make(FragmentTriggers.this.view, "Resetting", BaseTransientBottomBar.LENGTH_INDEFINITE);
                snack.show();
                reference.child("triggers")
                        .removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                snack.dismiss();
                                Toast.makeText(FragmentTriggers.this.activity, "Successfully Resetted", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}