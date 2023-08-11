package com.example.repairbrain20;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listener {

    ListView list;
    ImageView img;
    DatabaseReference list_effects_reference;
    Map<String,String> map;
    String type;
    Activity act;
    View view;

    Listener(Activity act,View view,String type)
    {
        this.act = act;
        this.view = view;

        this.list = view.findViewById(R.id.list);
        this.img = view.findViewById(R.id.no_results);

        this.type = type;

        list_effects_reference = User.getReference();

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

    public void delete_image_view()
    {
        img.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }

    public void addEffect()
    {
        /* EditText effect_view = new EditText(activity);
        effect_view.setHint("Enter the " + effect); */

        View view = View.inflate(act,R.layout.alert_dialog,null);
        EditText effect_view = view.findViewById(R.id.habit);

        new AlertDialog.Builder(act)
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String effect_new = effect_view.getText().toString().trim();

                        LocalDateTime date_time = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E,MMM dd yyyy");

                        String date_added =  date_time.format(formatter);

                        DatabaseReference reference = User.getReference();

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
        DatabaseReference reference = User.getReference();

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

class EffectsList
{
    String effect;
    List<String> list;

    EffectsList()
    {

    }

    EffectsList(String effect,String new_effect)
    {
        this.effect = effect+"_list";
        this.list.add(new_effect);
    }

    public String getEffect() {
        return effect;
    }

    public void addEffect(String effect)
    {
        this.list.add(effect);
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
