package com.example.repairbrain20;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class ActEvolution extends AppCompatActivity {

    CheckNetwork network_check;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects_tabs);

        TabLayout tabLayout = findViewById(R.id.tab);
        pager = findViewById(R.id.view_pager);

        network_check = new CheckNetwork(this, pager);

        Fragment pos = new FragmentPos();
        Fragment neg = new FragmentNeg();

        AdapterEffects adapter = new AdapterEffects(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add_fragment(pos, "Changes");
        adapter.add_fragment(neg, "Challenges");

        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        Intent intent = getIntent();
        if (intent != null) {
            tabLayout.selectTab(tabLayout.getTabAt(intent.getIntExtra("tab", 0)));
        }
    }

    @Override
    protected void onRestart() {
        network_check = new CheckNetwork(this, pager);
        network_check.register();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        network_check.register();
        super.onResume();
    }

    @Override
    protected void onPause() {
        network_check.unregister();
        super.onPause();
    }
}