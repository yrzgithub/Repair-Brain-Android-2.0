package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.fonts.FontStyle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HabitsAdapter extends BaseAdapter {

    Activity act;

    String[] days_list = {"Sun","Mon","Tue","Wed","Thur","Fri","Sat"};
    String today;
    boolean delete = false;
    int count = 0;
    TextView percent = null;
    ImageView up_or_down = null;
    TextView accuracy = null;

    List<String> keys;
    Map<String,ReplaceHabits> habits;
    String today_date;
    static int current_percent = 0;
    boolean[] states;
    static Map<String,ReplaceHabits> habits_copy;


    HabitsAdapter(Activity act, Map<String,ReplaceHabits> habits)
    {
        this.act = act;

        this.percent = act.findViewById(R.id.percent);
        this.up_or_down = act.findViewById(R.id.up_or_down);
        this.accuracy = act.findViewById(R.id.accuracy);

        if(habits.size()==0)
        {
            ImageView no_results = act.findViewById(R.id.no_results);
            ListView habit_list = act.findViewById(R.id.list);

            no_results.setVisibility(View.VISIBLE);
            habit_list.setVisibility(View.GONE);
        }

//        Log.e("uruttu_habits",habits.toString());

        this.habits = habits;
        habits_copy = habits;

        this.states = new boolean[habits.size()];
        Arrays.fill(this.states,false);

        keys = new ArrayList<>(habits.keySet());

        //Toast.makeText(act,String.valueOf(habits.keySet().size()),Toast.LENGTH_SHORT).show();

        today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("E"));

        LocalDateTime date_time = LocalDateTime.now();
        today_date = DateTimeFormatter.ofPattern("dd-MM-yy").format(date_time);
    }

    @SuppressLint("SetTextI18n")
    HabitsAdapter(Activity act, Map<String,ReplaceHabits> habits, boolean delete)
    {
        this(act,habits);
        this.delete = delete;

        if(delete)
        {
            if(habits.size()>0)
            {
                percent.setVisibility(View.GONE);
                up_or_down.setVisibility(View.GONE);
                accuracy.setText("Select Items To Remove");
            }
            else
            {
                percent.setVisibility(View.VISIBLE);
                up_or_down.setVisibility(View.VISIBLE);
                accuracy.setText("Replacing Accuracy :");
            }
        }
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @Override
    public Object getItem(int i) {
        return keys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = act.getLayoutInflater().inflate(R.layout.custom_habits_list,null,false);

        RelativeLayout main = view.findViewById(R.id.main);
        CheckBox check = view.findViewById(R.id.check);
        ImageView delete = view.findViewById(R.id.delete);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        view.setBackgroundResource(R.drawable.round_layout);

        String key = keys.get(i);

        TextView text = view.findViewById(R.id.habit);
        TextView show = view.findViewById(R.id.show_on);

        if(this.delete)
        {
            check.setVisibility(View.GONE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(HabitsAdapter.this.delete)
                    {
                        User.getReference()
                                .child("replace_habits")
                                .child(keys.get(i))
                                .removeValue()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(act,"Connection Failed",Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(act,"Habit Removed",Toast.LENGTH_SHORT).show();
                                            ListView view = act.findViewById(R.id.list);
                                            habits.remove(key);
                                            habits_copy = habits;

                                            view.setAdapter(new HabitsAdapter(act,habits,true));
                                        }
                                        else
                                        {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(act,message,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            });
        }
        else
        {
            delete.setVisibility(View.GONE);
            check.setVisibility(View.VISIBLE);
            boolean checked = states[i];
            check.setChecked(checked);

            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    states[i] = b;

                    if(HabitsAdapter.this.delete)
                    {
                        text.setPaintFlags(b?Paint.STRIKE_THRU_TEXT_FLAG:Paint.LINEAR_TEXT_FLAG);
                    }
                    else
                    {
                        update_percentage(b);
                        update_value(key,b);
                    }
                }
            });
        }


        ReplaceHabits habits = this.habits.get(key);

        if(habits==null) return view;

        List<String> show_on = habits.getShow_on();

        StringBuilder builder = new StringBuilder();

        for(String day : days_list)
        {
            if(show_on.contains(day))
            {
                builder.append(day).append(",").append(" ");
            }
        }

        String show_on_string = builder.deleteCharAt(builder.lastIndexOf(" ")).deleteCharAt(builder.lastIndexOf(",")).toString();

        text.setText(key);
        show.setText(show_on_string);

        return view;
    }

    public void update_value(String key,Boolean b)
    {
        User.getReference()
                .child("replace_habits")
                .child(key)
                .child("days_data")
                .child(today_date)
                .setValue(b?1:0);
    }

    public void update_percentage(boolean b)
    {
        if(b) ++count; else --count;

        int size = keys.size();

        if(size==0) return;

        int percent = (count * 100) / size;
        this.percent.setText(percent+"%");

        current_percent = percent;

        int diff = percent - HabitsAndAccuracy.last_accuracy_percent;

       if(diff>=0)
        {
            up_or_down.setImageResource(R.drawable.up);
        }
        else {
            up_or_down.setImageResource(R.drawable.down);
        }
    }
}
