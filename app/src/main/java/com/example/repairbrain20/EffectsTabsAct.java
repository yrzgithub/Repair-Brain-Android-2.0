package com.example.repairbrain20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class EffectsTabsAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects_tabs);

        TabLayout tabLayout = findViewById(R.id.tab);
        ViewPager pager = findViewById(R.id.view_pager);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(ConnectivityManager.class);
        cm.registerDefaultNetworkCallback(new CheckNetwork(this,pager));

        Fragment pos = new PosFragment();
        Fragment neg = new NegFragment();
        Fragment next = new NextFragment();

        EffectsAdapter adapter = new EffectsAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add_fragment(pos,"Positive");
        adapter.add_fragment(neg,"Negative");
        adapter.add_fragment(next,"Next");

        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
    }
}