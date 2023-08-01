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

        String id = User.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(id);
        DatabaseReference replace_habits_list =  reference.child("replace_habits_list");
        DatabaseReference replace_habits = reference.child(("replace_habits"));
        DatabaseReference replace_accuracy = reference.child("last_accuracy_percent");

        replace_accuracy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null)
                {
                    int lastly_noted_accuracy = snapshot.getValue(Integer.class);
                    last_accuracy_percent = lastly_noted_accuracy;
                    percent.setText(lastly_noted_accuracy+"%");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        replace_habits_list.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Object> list = (List<Object>) snapshot.getValue(new GenericTypeIndicator<Object>() {
                });
                if(list==null)
                {
                    img.setVisibility(View.VISIBLE);
                    list_view.setVisibility(View.GONE);
                    return;
                }

                img.setVisibility(View.GONE);
                list_view.setVisibility(View.VISIBLE);

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
                        list_view.setAdapter(new HabitsAdapter(getActivity(),map,list));
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