package com.example.repairbrain20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class StepsAct extends AppCompatActivity {

    Button prev_btn,next;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        next = findViewById(R.id.next);
        prev_btn = findViewById(R.id.prev);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference positive_reference = storage.getReferenceFromUrl("gs://repair-brain-20.appspot.com/negative effects.txt");
        StorageReference negative_reference = storage.getReferenceFromUrl("gs://repair-brain-20.appspot.com/positive effects.txt");
        StorageReference steps_reference = storage.getReferenceFromUrl("gs://repair-brain-20.appspot.com/steps.txt");

        Intent intent = getIntent();
        Intent go = new Intent(this,StepsAct.class);
        Intent prev = new Intent(this,StepsAct.class);

        Log.e("sanjay_t",String.valueOf(intent.getIntExtra("effect",-1)));

        switch (intent.getIntExtra("effect",-1))
        {
            case 0:
                new Listener(StepsAct.this,positive_reference,"Positive Effects : ");
                go.putExtra("effect",1);
                prev.putExtra("effect",3);
                break;

            case 1:
                new Listener(StepsAct.this,negative_reference,"Negative Effects : ");
                go.putExtra("effect",2);
                prev.putExtra("effect",0);
                break;

            case 2:
                new Listener(StepsAct.this,steps_reference,"Next Steps : ");
                go.putExtra("effect",3);
                prev.putExtra("effect",1);
                break;

            default:
                startActivity(new Intent(this,TimeActivity.class));
                prev.putExtra("effect",2);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(go);
            }
        });

        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(prev);
            }
        });
    }
}