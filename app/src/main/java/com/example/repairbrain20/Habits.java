package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class Habits extends AppCompatActivity {

    TextView percent;
    ListView list_view;
    ImageView img;
    static int last_accuracy_percent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habits);

        percent = findViewById(R.id.percent);
        list_view = findViewById(R.id.list);
        img = findViewById(R.id.img);

        img.setImageResource(R.drawable.noresultfound);

        User.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData data = snapshot.getValue(UserData.class);
                Habits.last_accuracy_percent = data.getLast_accuracy_percent();
                percent.setText(Habits.last_accuracy_percent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        User.getReference().child("replace_habits")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReplaceHabits replace_habits =  snapshot.getValue(ReplaceHabits.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static int lastly_accuracy()
    {
        return last_accuracy_percent;
    }
}