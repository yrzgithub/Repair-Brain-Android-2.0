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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listener {

    ListView list;
    DatabaseReference list_effects_reference;
    Map<String,String> map;
    String type;
    Activity act;
    View view;
    ImageView loading;
    DatabaseReference common_reference = FirebaseDatabase.getInstance().getReference();

    Listener(Activity act,View view,String type)
    {
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

        list_effects_reference = User.getAddictionReference();

        if(list_effects_reference!=null)
        {
            list_effects_reference.child(type).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                   if(task.isSuccessful())
                   {
                       map = task.getResult().getValue(new GenericTypeIndicator<Map<String, String>>() {
                           @NonNull
                           @Override
                           public String toString() {
                               return super.toString();
                           }
                       });

                       list.setVisibility(View.VISIBLE);
                       loading.setVisibility(View.GONE);

                       list.setAdapter(new PosNegNextAdapter(act,view,map,type));

                   }
                }
            });
        }
    }

    public Map<String,String> getEffectsMap()
    {
        return map;
    }

    public void addEffect()
    {
        /* EditText effect_view = new EditText(activity);
        effect_view.setHint("Enter the " + effect); */

        View view = View.inflate(act,R.layout.alert_dialog,null);
        AutoCompleteTextView search = view.findViewById(R.id.effects_list);
        search.setThreshold(0);

        search.setHint("Search or Enter");

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                search.showDropDown();
            }
        });

        DatabaseReference reference = User.getAddictionReference();

        if(reference!=null)
        {
            common_reference
                    .child("common_" + type)
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

                                List<String> suggestions = new ArrayList<>(map.keySet());

                            // Toast.makeText(act, "Common " + effect_name +" fetched",Toast.LENGTH_LONG).show();

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(act, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,suggestions);
                                search.setAdapter(adapter);
                        }
                    });
        }

        new AlertDialog.Builder(act)
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String effect_new = search.getText().toString();

                        if(effect_new.trim().equals("")) {
                            Toast.makeText(act.getApplicationContext(),"Invalid " + type.replace("_"," ").substring(0,type.length()-1),Toast.LENGTH_SHORT).show();
                            return;
                        }

                        LocalDateTime date_time = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E,MMM dd yyyy");

                        String date_added =  date_time.format(formatter);

                        DatabaseReference reference = User.getAddictionReference();

                        if(reference!=null)
                        {
                            Snackbar bar = Snackbar.make(list,"Adding",BaseTransientBottomBar.LENGTH_INDEFINITE);
                            bar.show();

                            reference.child(type)
                                    .child(effect_new)
                                    .setValue(date_added)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            bar.dismiss();
                                            reference.child("lastly_noted_"+type).setValue(effect_new);

                                            if(map==null) map = new HashMap<>();

                                            map.put(effect_new,date_added);

                                            Log.e("kathorathil",map.toString());

                                            Toast.makeText(act,"Successfully Added",Toast.LENGTH_LONG).show();
                                            list.setAdapter(new PosNegNextAdapter(act,Listener.this.view,map,type));
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }

    public void reset()
    {
        DatabaseReference reference = User.getAddictionReference();

        if(reference!=null)
        {
            Snackbar bar =  Snackbar.make(list,"Resetting", BaseTransientBottomBar.LENGTH_INDEFINITE);
            bar.show();
            reference.child(type).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    bar.dismiss();
                    Toast.makeText(act,"Successfully Resetted",Toast.LENGTH_SHORT).show();
                    list.setAdapter(new PosNegNextAdapter(act,view,null,type));
                }
            });
        }
    }
}
