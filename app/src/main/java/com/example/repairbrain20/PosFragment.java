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

import java.util.Map;

public class PosFragment extends Fragment {

    FirebaseDatabase database;

    PosFragment()
    {
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ImageView img = view.findViewById(R.id.no_results);
        ListView listView = view.findViewById(R.id.list);

        new Listener(getActivity(),database,img,listView,"Positive Effects");

        super.onViewCreated(view, savedInstanceState);
    }

    public static void show_image_view()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_effects, container, false);
    }
}