package com.example.repairbrain20;

import android.app.Activity;
import android.telephony.mbms.StreamingServiceInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Listener implements OnSuccessListener<Object>,OnFailureListener,ValueEventListener {

    ListView list;
    ImageView img;
    Activity act;
    DatabaseReference reference;
    DatabaseReference list_effects_reference;
    Map<String,Object> map;
    List<Object> list_effects;

    Listener(Activity act, ImageView img, ListView list,String type)
    {
        this.act = act;

        this.list = list;
        this.img = img;

        this.reference = User.getReference().child(type+"_list");
        list_effects_reference = User.getReference().child(type);

        list_effects_reference.addValueEventListener(this);
    }

    public void effect()
    {
        if(map!=null)
        {
           // list.setAdapter(new ListAdapter(act);
        }
        else
        {
            show_image_view(R.drawable.noresultfound);
        }
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

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        map = snapshot.getValue(new GenericTypeIndicator<Map<String, Object>>() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        });
       // Toast.makeText(act, "Data Changed", Toast.LENGTH_SHORT).show();

        Task<DataSnapshot> task = reference.get();
        task.addOnSuccessListener(this);
        task.addOnFailureListener(this);
    }

    @Override
    public void onCancelled(DatabaseError error) {
        show_image_view(R.id.no_results);
    }

    @Override
    public void onFailure(Exception e) {
        show_image_view(R.id.no_results);
    }

    @Override
    public void onSuccess(Object o) {
        //Toast.makeText(act, "Success", Toast.LENGTH_SHORT).show();
        DataSnapshot snapshot = (DataSnapshot) o;
        list_effects =  snapshot.getValue(new GenericTypeIndicator<List<Object>>() {
        });

        effect();
    }
}
