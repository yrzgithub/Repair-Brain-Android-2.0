package com.example.repairbrain20;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ActRepairsInsights extends AppCompatActivity {

    CheckNetwork network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_repairs);

        TabLayout tabs = findViewById(R.id.tabs);
        ViewPager pager = findViewById(R.id.pager);

        network = new CheckNetwork(this, tabs);

        FragmentRepairs repairs = new FragmentRepairs();
        FragmentInsights insights = new FragmentInsights();

        AdapterRepairsAndInsights adapter = new AdapterRepairsAndInsights(getSupportFragmentManager());
        adapter.add_tab(repairs, "Repairs");
        adapter.add_tab(insights, "Insights");

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }

    @Override
    protected void onResume() {
        network.register();
        super.onResume();
    }

    @Override
    protected void onPause() {
        network.unregister();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        super.onBackPressed();
    }
}