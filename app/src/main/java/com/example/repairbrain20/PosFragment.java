package com.example.repairbrain20;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PosFragment extends Fragment {

    FirebaseDatabase database;
    ListView listView = null;

    PosFragment()
    {
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ImageView img = view.findViewById(R.id.no_results);
        listView = view.findViewById(R.id.list);

        new Listener(getActivity(),database,img,listView,"Positive Effects");

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_effects, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add:
                addEffect();
                break;
        }
        return true;
    }

    public void addEffect()
    {
        String[] days = {"All","Sun","Mon","Tue","Wed","Thur","Fri","Sat"};

        List<String> show_on = new ArrayList<String>();

        View view = getLayoutInflater().inflate(R.layout.habits_add,null);

        EditText habit = view.findViewById(R.id.habit);

        CheckBox sun = view.findViewById(R.id.sun);
        CheckBox mon = view.findViewById(R.id.mon);
        CheckBox tue = view.findViewById(R.id.tue);
        CheckBox wed = view.findViewById(R.id.wed);
        CheckBox thur = view.findViewById(R.id.thur);
        CheckBox fri = view.findViewById(R.id.fri);
        CheckBox sat = view.findViewById(R.id.sat);
        CheckBox all = view.findViewById(R.id.all);

        CompoundButton.OnCheckedChangeListener checked_listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch (compoundButton.getId())
                {
                    case R.id.all:
                        if(b)
                        {
                            for(String day : days)
                            {
                                if(!show_on.contains(day))
                                {
                                    show_on.add(day);
                                }
                            }
                        }

                        sun.setChecked(true);
                        mon.setChecked(true);
                        tue.setChecked(true);
                        wed.setChecked(true);
                        thur.setChecked(true);
                        fri.setChecked(true);
                        sat.setChecked(true);

                        break;


                    case R.id.sun:
                        if(b)
                        {
                            show_on.add("Sun");
                        }
                        else
                        {
                            show_on.remove("Sun");
                        }
                        break;


                    case R.id.mon:
                        if(b)
                        {
                            show_on.add("Mon");
                        }
                        else
                        {
                            show_on.remove("Mon");
                        }
                        break;


                    case R.id.tue:
                        if(b)
                        {
                            show_on.add("Tue");
                        }
                        else
                        {
                            show_on.remove("Tue");
                        }
                        break;


                    case R.id.wed:
                        if(b)
                        {
                            show_on.add("Wed");
                        }
                        else
                        {
                            show_on.remove("Wed");
                        }
                        break;


                    case R.id.thur:
                        if(b)
                        {
                            show_on.add("Thur");
                        }
                        else
                        {
                            show_on.remove("Thur");
                        }
                        break;


                    case R.id.fri:
                        if(b)
                        {
                            show_on.add("Fri");
                        }
                        else
                        {
                            show_on.remove("Fri");
                        }
                        break;


                    case R.id.sat:
                        if(b)
                        {
                            show_on.add("Sat");
                        }
                        else
                        {
                            show_on.remove("Sat");
                        }
                        break;
                }
            }
        };

        all.setOnCheckedChangeListener(checked_listener);
        sun.setOnCheckedChangeListener(checked_listener);
        mon.setOnCheckedChangeListener(checked_listener);
        tue.setOnCheckedChangeListener(checked_listener);
        wed.setOnCheckedChangeListener(checked_listener);
        thur.setOnCheckedChangeListener(checked_listener);
        fri.setOnCheckedChangeListener(checked_listener);
        sat.setOnCheckedChangeListener(checked_listener);

        new AlertDialog.Builder(getActivity())
                .setView(view)
                .setIcon(R.drawable.ic_launcher_foreground)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create().show();
    }
}