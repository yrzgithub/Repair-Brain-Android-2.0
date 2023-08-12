package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.List;

public class CommonPosNegNextAct extends AppCompatActivity {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_pos_neg_next);

        list = findViewById(R.id.effects);

        Intent intent = getIntent();
        String type = intent.getStringExtra("effect");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("common_"+type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        List<String> list = task.getResult().getValue(new GenericTypeIndicator<List<String>>() {
                            @NonNull
                            @Override
                            public String toString() {
                                return super.toString();
                            }
                        });

                        CommonPosNegNextAdapter adapter = new CommonPosNegNextAdapter(CommonPosNegNextAct.this,list);
                        CommonPosNegNextAct.this.list.setAdapter(adapter);
                    }
                });
    }
}