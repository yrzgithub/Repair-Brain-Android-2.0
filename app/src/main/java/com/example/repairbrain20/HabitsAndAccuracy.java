package com.example.repairbrain20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class HabitsAndAccuracy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habits_and_accuracy_pager);

        ViewPager pager = findViewById(R.id.habits_pager);
        TabLayout tabs = findViewById(R.id.tabs);

        TimeFragment accuracy = new TimeFragment();
        HabitsWindow habits = new HabitsWindow();

        TimeAndHabitsAdapter adapter = new TimeAndHabitsAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add_tab(accuracy,"Accuracy");
        adapter.add_tab(habits,"Habits");

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }
}