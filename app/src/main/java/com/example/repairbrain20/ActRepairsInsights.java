package com.example.repairbrain20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ActRepairsInsights extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_repairs);

        TabLayout tabs = findViewById(R.id.tabs);
        ViewPager pager = findViewById(R.id.pager);

        FragmentRepairs repairs = new FragmentRepairs();
        FragmentInsights insights = new FragmentInsights();

        AdapterRepairsAndInsights adapter = new AdapterRepairsAndInsights(getSupportFragmentManager());
        adapter.add_tab(repairs,"Repairs");
        adapter.add_tab(insights,"Insights");

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        super.onBackPressed();
    }
}