package com.example.repairbrain20;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class HabitsWindow extends Fragment {

    TextView percent;
    ListView list_view;
    ImageView img;
    static int last_accuracy_percent = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_habits,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        percent = view.findViewById(R.id.percent);
        list_view = view.findViewById(R.id.list);
        img = view.findViewById(R.id.img);

        img.setImageResource(R.drawable.noresultfound);

        User.getReference().child("replace_habits")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String,ReplaceHabits> habits =  snapshot.getValue(new GenericTypeIndicator<Map<String, ReplaceHabits>>() {
                            @NonNull
                            @Override
                            public String toString() {
                                return super.toString();
                            }
                        });
                        list_view.setAdapter(new HabitsAdapter(getActivity(),habits));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}