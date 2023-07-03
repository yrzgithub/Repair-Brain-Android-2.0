package com.example.repairbrain20;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NextFragment extends Fragment {

    FirebaseDatabase database;

    NextFragment()
    {
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ImageView img = view.findViewById(R.id.no_results);
        ListView listView = view.findViewById(R.id.list);

        new Listener(getActivity(),database,img,listView,"Next Steps");

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_effects, container, false);
    }
}