package com.example.repairbrain20;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class ActEvolution extends AppCompatActivity {

    CheckNetwork network_check;
    ConnectivityManager cm;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects_tabs);

        TabLayout tabLayout = findViewById(R.id.tab);
        pager = findViewById(R.id.view_pager);

        network_check = new CheckNetwork(this, pager);
        cm = (ConnectivityManager) getSystemService(ConnectivityManager.class);

        Fragment pos = new FragmentPos();
        Fragment neg = new FragmentNeg();
        //Fragment next = new NextFragment();

        AdapterEffects adapter = new AdapterEffects(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add_fragment(pos, "Changes");
        adapter.add_fragment(neg, "Challenges");
        // adapter.add_fragment(next,"Next");

        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        Intent intent = getIntent();
        if (intent != null) {
            tabLayout.selectTab(tabLayout.getTabAt(intent.getIntExtra("tab", 0)));
        }

        // CheckNetwork.isAvailable(this,pager);
    }

    @Override
    protected void onResume() {
        cm.registerDefaultNetworkCallback(network_check);
        super.onResume();
    }

    @Override
    protected void onPause() {
        cm.unregisterNetworkCallback(network_check);
        super.onPause();
    }
}