package com.example.repairbrain20;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.Map;

public class FragmentRelapses extends Fragment {

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_relapses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        this.view = view;

        ListView list = view.findViewById(R.id.list);
        ImageView loading = view.findViewById(R.id.loading);

        Glide.with(getView()).load(R.drawable.loading_pink_list).into(loading);

        DatabaseReference reference = User.getRepairReference();
        reference.child("relapses")
            .get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                Map<String,Relapse> map = task.getResult().getValue(new GenericTypeIndicator<Map<String, Relapse>>() {
                                    @NonNull
                                    @Override
                                    public String toString() {
                                        return super.toString();
                                    }
                                });
                                AdapterRelapses relapse_adapter = new AdapterRelapses(getActivity(),FragmentRelapses.this.view,map);
                                list.setAdapter(relapse_adapter);
                            }
                        }
                    });

        super.onViewCreated(view, savedInstanceState);
    }
}