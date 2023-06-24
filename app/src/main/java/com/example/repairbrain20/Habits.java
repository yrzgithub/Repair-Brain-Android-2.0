package com.example.repairbrain20;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.Map;

public class Habits extends AppCompatActivity {

    TextView percent;
    ListView list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habits);

        percent = findViewById(R.id.percent);
        list_view = findViewById(R.id.list);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("data");
        DatabaseReference replace_habits_list =  reference.child("replace_habits_list");
        DatabaseReference replace_habits = reference.child(("replace_habits"));

        replace_habits_list.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Object> list = (List<Object>) snapshot.getValue(new GenericTypeIndicator<Object>() {
                });
                Log.e("sanjay_task",list.toString());
                replace_habits.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Map<String,Object> map = snapshot.getValue(new GenericTypeIndicator<Map<String, Object>>() {
                            @Override
                            public int hashCode() {
                                return super.hashCode();
                            }
                        });
                        Log.e("sanjay_task",map.toString());
                        list_view.setAdapter(new HabitsAdapter(Habits.this,map,list));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}