package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

public class ActTimeAndAccuracy extends AppCompatActivity implements View.OnClickListener {

    static int last_accuracy_percent = 0;
    ViewPager pager;
    CheckNetwork network_check;
    ConnectivityManager cm;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_accuracy);

        DrawerLayout drawer = findViewById(R.id.drawer);

        toggle = new ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close);
        toggle.syncState();

        drawer.addDrawerListener(toggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pager = findViewById(R.id.habits_pager);
        TabLayout tabs = findViewById(R.id.tabs);

        network_check =  new CheckNetwork(this,pager);
        cm = (ConnectivityManager) getSystemService(ConnectivityManager.class);

     /*   ConnectivityManager cm = (ConnectivityManager) getSystemService(ConnectivityManager.class);
        cm.registerDefaultNetworkCallback(new CheckNetwork(this,pager)); */

        FragmentTime accuracy = new FragmentTime();
        FragmentHabits habits = new FragmentHabits();

        AdapterTimeAndHabits adapter = new AdapterTimeAndHabits(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add_tab(accuracy,"TIME GONE");
        adapter.add_tab(habits,"ACCURACY");

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        // DrawerLayout views

        /*
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(HabitsAndAccuracy.this,"clicked",Toast.LENGTH_SHORT).show();
                }
            });
         */

        LinearLayout home = findViewById(R.id.home);
        LinearLayout time_gone = findViewById(R.id.time_gone);
        LinearLayout effects = findViewById(R.id.effects);
        LinearLayout addictions = findViewById(R.id.addictions);
        LinearLayout about = findViewById(R.id.about);
        LinearLayout contact_developer = findViewById(R.id.contact_developer);
        LinearLayout logout = findViewById(R.id.logout);

        home.setOnClickListener(this);
        time_gone.setOnClickListener(this);
        effects.setOnClickListener(this);
        addictions.setOnClickListener(this);
        about.setOnClickListener(this);
        contact_developer.setOnClickListener(this);
        logout.setOnClickListener(this);

        Intent intent = getIntent();
        if(intent!=null)
        {
            tabs.selectTab(tabs.getTabAt(intent.getIntExtra("tab",0)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;

        if(toggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onStop() {
        // User.getReference().child("last_accuracy_percent").setValue(HabitsAdapter.current_percent);
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId())
        {
            case R.id.home:
                intent = new Intent(this, ActMain.class);
                startActivity(intent);
                break;

            case R.id.time_gone:
                intent = new Intent(this, ActTimeAndAccuracy.class);
                intent.putExtra("tab",0);
                startActivity(intent);
                break;

            case R.id.addictions:
                intent = new Intent(this, ActAddictionsSteps.class);
                startActivity(intent);
                break;

            case R.id.effects:
                intent = new Intent(this, ActEffectsTabs.class);
                intent.putExtra("tab",0);
                startActivity(intent);
                break;

            case R.id.about:
                break;

            case R.id.contact_developer:
                break;

            case R.id.logout:
                SharedPreferences preferences = getSharedPreferences("login_data",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().apply();

                intent = new Intent(this, ActLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }
}