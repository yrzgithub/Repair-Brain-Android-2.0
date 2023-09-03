package com.example.repairbrain20;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.Map;

public class ActContactDeveloper extends AppCompatActivity {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("contact");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_contact_developer);

        ListView list = findViewById(R.id.list);
        ImageView loading = findViewById(R.id.loading);

        Glide.with(this).load(R.drawable.loading_pink_list).into(loading);

        if(reference!=null)
        {
            reference
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                loading.setVisibility(View.GONE);
                                list.setVisibility(View.VISIBLE);
                                Map<String,Contact> map =  task.getResult().getValue(new GenericTypeIndicator<Map<String, Contact>>() {
                                    @NonNull
                                    @Override
                                    public String toString() {
                                        return super.toString();
                                    }
                                });

                                list.setAdapter(new AdapterListDeveloperContact(ActContactDeveloper.this,map));
                            }
                            else
                            {
                                Glide.with(ActContactDeveloper.this).load(R.drawable.noresultfound).into(loading);
                            }
                        }
                    });
        }
    }
}