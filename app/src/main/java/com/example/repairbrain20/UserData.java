package com.example.repairbrain20;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserData {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    int last_accuracy_percent = 0;
    String uid,lastly_noted_change,lastly_noted_side_effect,next_step;
    Time lastly_opened,start_time;
    Object lastly_relapsed;
    Map<String,String> negative_effects,positive_effects,next_steps;
    List<String> negative_effects_list,positive_effects_list,next_steps_list,replace_habits_list;
    Plot accuracy_plot;

    public void setLastly_relapsed(Time lastly_relapsed) {
        this.lastly_relapsed = lastly_relapsed;
    }

    public Plot getAccuracy_plot() {
        return accuracy_plot;
    }

    public void setAccuracy_plot(Plot accuracy_plot) {
        this.accuracy_plot = accuracy_plot;
    }

    public ReplaceHabits getReplace_habits() {
        return replace_habits;
    }

    public void setReplace_habits(ReplaceHabits replace_habits) {
        this.replace_habits = replace_habits;
    }

    ReplaceHabits replace_habits;

    public UserData() {

    }

    UserData(FirebaseUser user)
    {
        this.uid = user.getUid();
        reference = reference.child(this.uid);

        lastly_noted_change = lastly_noted_side_effect  = next_step = "Not Found";
        negative_effects = positive_effects = next_steps = new HashMap<>();
        negative_effects_list = positive_effects_list = next_steps_list = replace_habits_list = new ArrayList<>();
        lastly_opened = start_time = new Time(LocalDateTime.now());
        lastly_relapsed = "Not found";
    }

    public void write()
    {
        reference.setValue(this, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

            }
        });
    }

    public int getLast_accuracy_percent() {
        return last_accuracy_percent;
    }

    public void setLast_accuracy_percent(int last_accuracy_percent) {
        this.last_accuracy_percent = last_accuracy_percent;
    }

    public String getLastly_noted_change() {
        return lastly_noted_change;
    }

    public void setLastly_noted_change(String lastly_noted_change) {
        this.lastly_noted_change = lastly_noted_change;
    }

    public String getLastly_noted_side_effect() {
        return lastly_noted_side_effect;
    }

    public void setLastly_noted_side_effect(String lastly_noted_side_effect) {
        this.lastly_noted_side_effect = lastly_noted_side_effect;
    }

    public Object getLastly_relapsed() {
        return lastly_relapsed;
    }

    public void setLastly_relapsed(String lastly_relapsed) {
        this.lastly_relapsed = lastly_relapsed;
    }

    public String getNext_step() {
        return next_step;
    }

    public void setNext_step(String next_step) {
        this.next_step = next_step;
    }

    public Time getLastly_opened() {
        return lastly_opened;
    }

    public void setLastly_opened(Time lastly_opened) {
        this.lastly_opened = lastly_opened;
    }

    public Time getStart_time() {
        return start_time;
    }

    public void setStart_time(Time start_time) {
        this.start_time = start_time;
    }

    public Map<String, String> getNegative_effects() {
        return negative_effects;
    }

    public void setNegative_effects(Map<String, String> negative_effects) {
        this.negative_effects = negative_effects;
    }

    public Map<String, String> getPositive_effects() {
        return positive_effects;
    }

    public void setPositive_effects(Map<String, String> positive_effects) {
        this.positive_effects = positive_effects;
    }

    public Map<String, String> getNext_steps() {
        return next_steps;
    }

    public void setNext_steps(Map<String, String> next_steps) {
        this.next_steps = next_steps;
    }

    public List<String> getNegative_effects_list() {
        return negative_effects_list;
    }

    public void setNegative_effects_list(List<String> negative_effects_list) {
        this.negative_effects_list = negative_effects_list;
    }

    public List<String> getPositive_effects_list() {
        return positive_effects_list;
    }

    public void setPositive_effects_list(List<String> positive_effects_list) {
        this.positive_effects_list = positive_effects_list;
    }

    public List<String> getNext_steps_list() {
        return next_steps_list;
    }

    public void setNext_steps_list(List<String> next_steps_list) {
        this.next_steps_list = next_steps_list;
    }

    public List<String> getReplace_habits_list() {
        return replace_habits_list;
    }

    public void setReplace_habits_list(List<String> replace_habits_list) {
        this.replace_habits_list = replace_habits_list;
    }
}

class Time
{
    int day,hour,minute,second,month,year;

    public Time()
    {

    }

    public Time(int year, int month, int day, int hour, int minute, int second) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.month = month;
        this.year = year;
    }

    public Time(LocalDateTime date_time)
    {
        this.day = date_time.getDayOfMonth();
        this.day = date_time.getHour();
        this.hour = date_time.getHour();
        this.minute = date_time.getMinute();
        this.second = date_time.getSecond();
        this.month = date_time.getMonthValue();
        this.year = date_time.getYear();
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}

class Plot
{
    List<Integer> date,value;

    Plot()
    {

    }

    public Plot(List<Integer> date, List<Integer> value) {
        this.date = date;
        this.value = value;
    }

    public List<Integer> getDate() {
        return date;
    }

    public void setDate(List<Integer> date) {
        this.date = date;
    }

    public List<Integer> getValue() {
        return value;
    }

    public void setValue(List<Integer> value) {
        this.value = value;
    }
}

class ReplaceHabits
{
    String habit;
    Map<String,Integer> days_data;
    List<String> show_on;

    public ReplaceHabits()
    {

    }

    public ReplaceHabits(String habit, Map<String, Integer> days_data, List<String> show_on) {
        this.habit = habit;
        this.days_data = days_data;
        this.show_on = show_on;
    }

    public String getHabit() {
        return habit;
    }

    public void setHabit(String habit) {
        this.habit = habit;
    }

    public Map<String, Integer> getDays_data() {
        return days_data;
    }

    public void setDays_data(Map<String, Integer> days_data) {
        this.days_data = days_data;
    }

    public List<String> getShow_on() {
        return show_on;
    }

    public void setShow_on(List<String> show_on) {
        this.show_on = show_on;
    }
}


