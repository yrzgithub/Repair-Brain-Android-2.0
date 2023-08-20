package com.example.repairbrain20;

import static com.example.repairbrain20.R.*;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentTriggers extends Fragment {

    ListView list;
    ImageView loading;
    View view;

    public FragmentTriggers() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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

        Glide.with(getActivity()).load(drawable.loading_pink_list).into(loading);

        DatabaseReference reference = User.getRepairReference();

        if(reference!=null)
        {
            reference.child("triggers/"+User.selected_addiction)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Map<String,String> map =  task.getResult().getValue(new GenericTypeIndicator<Map<String, String>>() {
                                @NonNull
                                @Override
                                public String toString() {
                                    return super.toString();
                                }
                            });

                            list.setAdapter(new AdapterTriggers(getActivity(),FragmentTriggers.this.view,map));

                        }
                    });
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.update_database_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.add:
                View view = getLayoutInflater().inflate(R.layout.alert_dialog,null);
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
                                Map<String,Common> map = task.getResult().getValue(new GenericTypeIndicator<Map<String, Common>>() {
                                    @NonNull
                                    @Override
                                    public String toString() {
                                        return super.toString();
                                    }
                                });

                                if(map!=null)
                                {
                                    List<String> list = new ArrayList<>(map.keySet());
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,list);
                                    triggers_view.setAdapter(adapter);
                                }
                            }
                        });

                DatabaseReference reference = User.getRepairReference();

                if(reference!=null)
                {
                    new AlertDialog.Builder(getActivity())
                            .setView(view)
                            .setNegativeButton("Cancel",null)
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String trigger_name = triggers_view.getText().toString();

                                    LocalDateTime local_time = LocalDateTime.now();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

                                    String time_ = local_time.format(formatter);

                                    Snackbar snack = Snackbar.make(FragmentTriggers.this.view,"Adding", BaseTransientBottomBar.LENGTH_INDEFINITE);
                                    snack.show();

                                    reference
                                            .child("triggers")
                                            .child(trigger_name)
                                            .setValue(time_)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    snack.dismiss();
                                                    Toast.makeText(getActivity(),"Trigger added",Toast.LENGTH_SHORT).show();

                                                    Map<String,String> map =  AdapterTriggers.triggers_copy;
                                                    map.put(trigger_name,time_);

                                                    AdapterTriggers adapter = new AdapterTriggers(getActivity(),FragmentTriggers.this.view,map);
                                                    list.setAdapter(adapter);
                                                }
                                            });
                                }
                            })
                            .create()
                            .show();
                }
                break;

            case id.remove:
                Map<String,String> copy = AdapterTriggers.triggers_copy;
                if(copy==null || copy.size()==0)
                {
                    Toast.makeText(getActivity(),"Triggers list is empty",Toast.LENGTH_SHORT).show();
                }
                list.setAdapter(new AdapterTriggers(getActivity(),FragmentTriggers.this.view,true));
                break;

            case id.common:
                Intent intent = new Intent(getActivity(),ActCommon.class);
                intent.putExtra("common","common_triggers/"+User.selected_addiction);
                startActivity(intent);
                break;

            case id.reset:
                reference = User.getRepairReference();
                Snackbar snack = Snackbar.make(FragmentTriggers.this.view,"Resetting",BaseTransientBottomBar.LENGTH_INDEFINITE);
                snack.show();
                reference.child("triggers")
                        .removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                snack.dismiss();
                                Toast.makeText(getActivity(),"Successfully Resetted",Toast.LENGTH_SHORT).show();
                                AdapterTriggers adapter = new AdapterTriggers(getActivity(),FragmentTriggers.this.view,new HashMap<>());
                            }
                        });
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}