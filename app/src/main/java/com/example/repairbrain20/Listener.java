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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Listener {

    ListView list;
    ImageView img;
    Activity act;
    DatabaseReference reference;
    DatabaseReference list_effects_reference;
    Map<String,String> map;
    boolean delete;

    Listener(Activity act, ImageView img, ListView list,String type)
    {
        this.act = act;

        this.list = list;
        this.img = img;

        list_effects_reference = User.getReference().child(type);

        list_effects_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                map = snapshot.getValue(new GenericTypeIndicator<Map<String, String>>() {
                    @NonNull
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                });

                if(map==null)
                {
                    show_image_view(R.drawable.noresultfound);
                }
                else
                {
                    list.setAdapter(new PosNegNextAdapter(act,map));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                show_image_view(R.drawable.noresultfound);
            }
        });
    }

    public void show_image_view(int id)
    {
        img.setVisibility(View.VISIBLE);
        img.setForegroundGravity(Gravity.CENTER);
        list.setVisibility(View.GONE);
        Glide.with(act).load(id).into(img);
    }

    public void delete_image_view()
    {
        img.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }

    public static void addEffect(Activity activity,String effect)
    {
        /* EditText effect_view = new EditText(activity);
        effect_view.setHint("Enter the " + effect); */

        View view = View.inflate(activity,R.layout.alert_dialog,null);
        EditText effect_view = view.findViewById(R.id.habit);

        new AlertDialog.Builder(activity)
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String effect_new = effect_view.getText().toString();

                        LocalDateTime date_time = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E,MMM dd yyyy");

                        String date_added =  date_time.format(formatter);

                        DatabaseReference reference = User.getReference();

                        if(reference!=null)
                        {
                            reference.child(effect)
                                    .child(effect_new)
                                    .setValue(date_added)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            reference.child("lastly_noted_"+effect)
                                                    .setValue(effect_new);
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }

    public static void reset(Activity act,String effect_name)
    {
        DatabaseReference reference = User.getReference();

        if(reference!=null)
        {
            reference.child(effect_name).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Toast.makeText(act,"Successfully Resetted",Toast.LENGTH_SHORT).show();
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
