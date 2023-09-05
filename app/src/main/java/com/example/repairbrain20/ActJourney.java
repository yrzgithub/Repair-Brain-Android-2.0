package com.example.repairbrain20;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class ActJourney extends AppCompatActivity {

    CheckNetwork check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_journey);

        TabLayout tabs = findViewById(R.id.tabs);
        ViewPager pager = findViewById(R.id.view_pager);

        check = new CheckNetwork(this,pager);

        AdapterJourney adapter = new AdapterJourney(getSupportFragmentManager());
        adapter.add_tab(new FragmentRelapses(), "Relapses");
        adapter.add_tab(new FragmentSteps(), "Steps");

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

    }

    @Override
    protected void onResume() {
        check.register();
        super.onResume();
    }

    @Override
    protected void onPause() {
        check.unregister();
        super.onPause();
    }
}