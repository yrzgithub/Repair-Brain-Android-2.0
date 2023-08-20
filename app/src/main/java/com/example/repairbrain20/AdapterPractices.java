package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterPractices extends BaseAdapter {

    Activity act;

    String[] days_list = {"Sun","Mon","Tue","Wed","Thur","Fri","Sat"};
    String today;
    boolean delete = false;
    int count = 0;
    TextView percent = null;
    ImageView up_or_down = null;
    TextView accuracy = null;
    List<String> keys = new ArrayList<>();
    Map<String,ReplaceHabits> habits = new HashMap<>();
    String today_date;
    static int current_percent = 0;
    boolean[] states;
    static Map<String,ReplaceHabits> habits_copy = new HashMap<>();
    DatabaseReference reference;
    LinearLayout main;
    View view;
    ImageView no_results;
    ListView habits_list;
    Snackbar snack;
    String today_day;
    List<String> enabled_keys = new ArrayList<>();
    int enabled_key_size = 1;


    AdapterPractices(Activity act, View view, Map<String,ReplaceHabits> habits)
    {
        this.act = act;
        this.view = view;

        main = view.findViewById(R.id.main);

        this.percent = view.findViewById(R.id.percent);
        this.up_or_down = view.findViewById(R.id.up_or_down);
        this.accuracy = view.findViewById(R.id.accuracy);

        this.no_results = view.findViewById(R.id.no_results);
        this.habits_list = view.findViewById(R.id.list);

        accuracy.setText("Replacing Accuracy :");
        percent.setText("0%");

        today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("E"));

        LocalDateTime date_time = LocalDateTime.now();
        today_date = DateTimeFormatter.ofPattern("dd-MM-yy").format(date_time);
        today_day = DateTimeFormatter.ofPattern("E").format(date_time);

        if(habits!=null) this.habits.putAll(habits);

        if (this.habits.size() > 0) {
            habits_list.setVisibility(View.VISIBLE);
            no_results.setVisibility(View.GONE);
            up_or_down.setVisibility(View.VISIBLE);
            percent.setVisibility(View.VISIBLE);
        }

        else
        {
            no_results.setVisibility(View.VISIBLE);
            habits_list.setVisibility(View.GONE);
            Glide.with(act).load(R.drawable.noresultfound).into(no_results);
        }

        try {
            snack = Snackbar.make(main,"Reload the list",BaseTransientBottomBar.LENGTH_INDEFINITE);
            snack.setAction("Reload", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    act.recreate();
                }
            });
        }
        catch (Exception e){

        }

        for(Map.Entry<String,ReplaceHabits> e : this.habits.entrySet())
        {
            List<String> show_on = e.getValue().getShow_on();
            String habit_name = e.getKey();

            if(show_on.contains(today_day))
            {
                enabled_keys.add(habit_name);
            }
        }

        this.states = new boolean[this.habits.size()];
        Arrays.fill(this.states,false);

        keys.addAll(this.habits.keySet());

        enabled_key_size = enabled_keys.size();

        habits_copy.clear();
        habits_copy.putAll(this.habits);

        reference = User.getRepairReference();
    }

    @SuppressLint("SetTextI18n")
    AdapterPractices(Activity act, View view, boolean delete)
    {
        this(act,view,habits_copy);
        this.delete = delete;

        if(delete)
        {
            percent.setText("0%");

            if(keys.size()>0)
            {
                up_or_down.setVisibility(View.GONE);
                percent.setVisibility(View.GONE);
                accuracy.setText("Select Items To Remove");
                if(snack!=null) snack.show();
            }
            else
            {
                up_or_down.setVisibility(View.VISIBLE);
                percent.setVisibility(View.VISIBLE);
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

        TextView show_on_ = view.findViewById(R.id.show_on_text);
        TextView text = view.findViewById(R.id.habit);
        TextView show = view.findViewById(R.id.show_on);

        String key = keys.get(i);

        ReplaceHabits habits = this.habits.get(key);

        List<String> show_on = habits.getShow_on();
        boolean habit_enabled = show_on.contains(today_day);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        view.setBackgroundResource(R.drawable.round_layout);

        if(this.delete)
        {
            check.setVisibility(View.GONE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(AdapterPractices.this.delete)
                    {
                        Snackbar bar = Snackbar.make(main,"Removing", BaseTransientBottomBar.LENGTH_INDEFINITE);
                        bar.show();

                        User.getRepairReference()
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

                                        bar.dismiss();

                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(act,"Habit Removed",Toast.LENGTH_SHORT).show();
                                            ListView view = AdapterPractices.this.view.findViewById(R.id.list);
                                            AdapterPractices.this.habits.remove(key);
                                            AdapterPractices.habits_copy = AdapterPractices.this.habits;

                                            view.setAdapter(new AdapterPractices(act, AdapterPractices.this.view,true));
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

                    if(!AdapterPractices.this.delete)
                    {
                        if(habit_enabled)
                        {
                            update_percentage(b);
                            update_value(key,b);
                        }
                    }
                }
            });
        }

        if(!habit_enabled && !this.delete)
        {
            main.setEnabled(false);
            check.setEnabled(false);
            delete.setEnabled(false);

            text.setTextColor(Color.LTGRAY);
        }

        StringBuilder builder = new StringBuilder();

        for(String day : days_list)
        {
            if(show_on.contains(day))
            {
                builder.append(day).append(",").append(" ");
            }
        }

        String show_on_string = builder.delete(builder.length()-2,builder.length()-1).toString().trim(); //deleteCharAt(builder.lastIndexOf(" ")).deleteCharAt(builder.lastIndexOf(","))

        String show_ = key.substring(0,1).toUpperCase() + key.substring(1);
        text.setText(show_);
        show.setText(show_on_string);

        return view;
    }

    public void update_value(String key,Boolean b)
    {
        if(reference!=null)
        {
            reference
                    .child("replace_habits")
                    .child(key)
                    .child("days_data")
                    .child(today_date)
                    .setValue(b?1:0);
        }
    }

    public void update_percentage(boolean b)
    {
        if(b) ++count; else --count;

        int size = enabled_key_size;

        if(size==0) return;

        int percent = (count * 100) / size;
        this.percent.setText(percent+"%");

        if(reference!=null)
        {
            reference
                    .child("last_accuracy_percent")
                    .setValue(percent);
        }

        current_percent = percent;

        int diff = percent - ActRecovery.last_accuracy_percent;
        Log.e("last_accuracy_uruttu",String.valueOf(diff + " " + ActRecovery.last_accuracy_percent));

       if(diff>=0)
        {
            up_or_down.setImageResource(R.drawable.up);
        }
        else {
            up_or_down.setImageResource(R.drawable.down);
        }
    }
}
