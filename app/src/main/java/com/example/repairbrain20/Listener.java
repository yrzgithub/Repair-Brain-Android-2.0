package com.example.repairbrain20;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class Listener {

    ListView list;
    ImageView img;
    Activity act;
    DatabaseReference reference;
    DatabaseReference list_effects_reference;
    Map<String,String> map;

    Listener(Activity act, ImageView img, ListView list,String type)
    {
        this.act = act;

        this.list = list;
        this.img = img;

        this.reference = User.getReference().child(type+"_list");
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
