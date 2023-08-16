package com.example.repairbrain20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class ActAddictionsSteps extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        TabLayout tabs = findViewById(R.id.tabs);
        ViewPager pager = findViewById(R.id.pager);

        AdapterAddictionsSteps adapter = new AdapterAddictionsSteps(getSupportFragmentManager());
        adapter.add_tab(new FragmentAddiction(),"Addictions");
        adapter.add_tab(new FragmentNext(),"Steps");

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }
}