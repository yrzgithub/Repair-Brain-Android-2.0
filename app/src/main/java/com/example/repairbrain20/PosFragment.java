package com.example.repairbrain20;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

public class PosFragment extends Fragment {

    ListView listView = null;
    DatabaseReference reference;

    PosFragment()
    {
        reference = User.getReference();
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

        new Listener(getActivity(),img,listView,"positive_effects");

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
                addEffect("positive_effects");
                break;
        }
        return true;
    }

    public void addEffect(String effect)
    {
        EditText effect_view = new EditText(getActivity());

        new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher_foreground)
                .setTitle("Repair Brain")
                .setMessage("Best Of Luck")
                .setView(effect_view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String effect_new = effect_view.getText().toString();

                        LocalDateTime date_time = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E,MMM dd yyyy");

                        String date_added =  date_time.format(formatter);

                        reference.child(effect)
                                .child(effect_new)
                                .setValue(date_added)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.e("sanjay_effect",effect_new+" added");
                                        save_effect(effect,effect_new);
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }

    public void save_effect(String effect,String effect_new)
    {
        reference
                .child(effect+"_list")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        EffectsList list = snapshot.getValue(EffectsList.class);
                        list.addEffect(effect_new);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void addHabit(Activity act,LayoutInflater inflater)
    {
        String[] days = {"Sun","Mon","Tue","Wed","Thur","Fri","Sat"};

        List<String> show_on = new ArrayList<String>();

        View view = inflater.inflate(R.layout.habits_add,null);

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
                            show_on.clear();
                            sun.setChecked(true);
                            mon.setChecked(true);
                            tue.setChecked(true);
                            wed.setChecked(true);
                            thur.setChecked(true);
                            fri.setChecked(true);
                            sat.setChecked(true);
                        }
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

        new AlertDialog.Builder(act)
                .setView(view)
                .setIcon(R.drawable.ic_launcher_foreground)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String habit_ = habit.getText().toString();

                        if(habit_.trim().equals(""))
                        {
                            Toast.makeText(act,"Habit cannot be empty",Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(show_on.size()==0)
                        {
                            Toast.makeText(act,"Days not selected",Toast.LENGTH_LONG).show();
                            return;
                        }

                        Map<String, Integer> days_data = new HashMap<>();

                        ReplaceHabits replace = new ReplaceHabits(days_data,show_on);

                        User.getReference().child("replace_habits").child(habit_).setValue(replace)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.e("sanjay",show_on.toString());
                                    }
                                });
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