package com.example.repairbrain20;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentRepairs extends Fragment {

    ListView list;
    ImageView no_results;
    Map<String, Repairs> addictions;
    View view;
    Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        FragmentRepairs.this.activity = getActivity();
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repairs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        AdapterRepairsList.delete = false;

        list = view.findViewById(R.id.list);
        no_results = view.findViewById(R.id.no_results);

        Glide.with(this)
                .load(R.drawable.loading_pink_list)
                .into(no_results);

        DatabaseReference reference = User.getMainReference();

        if (reference == null && !activity.isDestroyed()) {
            Glide.with(activity).load(R.drawable.noresultfound).into(no_results);
            return;
        }

        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        addictions = snapshot.getValue(new GenericTypeIndicator<Map<String, Repairs>>() {
                            @NonNull
                            @Override
                            public String toString() {
                                return super.toString();
                            }
                        });

                        if (addictions != null) {
                            addictions.remove("insights");
                        }

                        if (AdapterRepairsList.delete)
                            list.setAdapter(new AdapterRepairsList(FragmentRepairs.this.activity, FragmentRepairs.this.view, addictions, true));
                        else
                            list.setAdapter(new AdapterRepairsList(FragmentRepairs.this.activity, FragmentRepairs.this.view, addictions));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Glide.with(FragmentRepairs.this).load(R.drawable.noresultfound).into(no_results);
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:
                View view = View.inflate(FragmentRepairs.this.activity, R.layout.alert_dialog, null);

                AutoCompleteTextView addiction_edit = view.findViewById(R.id.effects_list);
                addiction_edit.setHint("Search or Enter");
                addiction_edit.setThreshold(0);

                addiction_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        addiction_edit.showDropDown();
                    }
                });

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("common_addictions")
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

                                    ArrayAdapter<String> common_list = new ArrayAdapter<>(FragmentRepairs.this.activity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list);
                                    addiction_edit.setAdapter(common_list);
                                }
                            }
                        });

                new AlertDialog.Builder(FragmentRepairs.this.activity)
                        .setView(view)
                        .setNegativeButton("cancel", null)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Repairs addiction = new Repairs(LocalDateTime.now());
                                String addiction_ = addiction_edit.getText().toString().trim();

                                if (!Data.isValidKey(addiction_)) {
                                    Toast.makeText(FragmentRepairs.this.activity, "Invalid Repair", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                DatabaseReference main_reference = User.getMainReference();

                                if (main_reference != null) {

                                    Snackbar snack = Snackbar.make(list, "Adding", BaseTransientBottomBar.LENGTH_INDEFINITE);
                                    snack.show();

                                    main_reference
                                            .child(addiction_)
                                            .setValue(addiction)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    User.setAddiction(FragmentRepairs.this.activity, addiction_);
                                                    snack.dismiss();
                                                }
                                            });
                                }
                            }
                        })
                        .show();
                break;

            case R.id.common:
                Intent intent = new Intent(FragmentRepairs.this.activity, ActCommon.class);
                intent.putExtra("common", "common_addictions");
                startActivity(intent);
                break;

            case R.id.remove:
                list.setAdapter(new AdapterRepairsList(FragmentRepairs.this.activity, FragmentRepairs.this.view, addictions, true));
                break;

            case R.id.reset:
                reference = User.getMainReference();
                if (reference != null) {
                    Snackbar snack = Snackbar.make(list, "Resetting", BaseTransientBottomBar.LENGTH_INDEFINITE);
                    snack.show();

                    reference.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            snack.dismiss();
                            Toast.makeText(FragmentRepairs.this.activity, "Successfully resetted", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;

            case R.id.settings:
                startActivity(new Intent(FragmentRepairs.this.activity, ActSettings.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.repair_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}