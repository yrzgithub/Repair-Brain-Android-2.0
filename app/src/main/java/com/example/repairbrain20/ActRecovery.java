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

public class ActRecovery extends AppCompatActivity implements View.OnClickListener {

    static int last_accuracy_percent = 0;
    ViewPager pager;
    CheckNetwork network_check;
    ConnectivityManager cm;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

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

        FragmentProgress progress = new FragmentProgress();
        FragmentTriggers triggers = new FragmentTriggers();
        //FragmentSteps steps = new FragmentSteps();
        FragmentPractices practices = new FragmentPractices();

        AdapterTimeAndHabits adapter = new AdapterTimeAndHabits(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.add_tab(progress,"Progress");
        adapter.add_tab(triggers,"Triggers");
        //adapter.add_tab(steps,"Steps");
        adapter.add_tab(practices,"Practices");

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
        LinearLayout recovery = findViewById(R.id.recovery);
        LinearLayout effects = findViewById(R.id.effects);
        LinearLayout repairs = findViewById(R.id.repairs);
        LinearLayout journey = findViewById(R.id.journey);
        LinearLayout about = findViewById(R.id.about);
        LinearLayout contact_developer = findViewById(R.id.contact_developer);
        LinearLayout logout = findViewById(R.id.logout);

        home.setOnClickListener(this);
        recovery.setOnClickListener(this);
        effects.setOnClickListener(this);
        repairs.setOnClickListener(this);
        journey.setOnClickListener(this);
        about.setOnClickListener(this);
        contact_developer.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

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
                intent = new Intent(this, ActHome.class);
                startActivity(intent);
                break;

            case R.id.recovery:
                intent = new Intent(this, ActRecovery.class);
                startActivity(intent);
                break;

            case R.id.effects:
                intent = new Intent(this, ActEffects.class);
                startActivity(intent);
                break;

            case R.id.journey:
                intent = new Intent(this, ActRelapses.class);
                startActivity(intent);
                break;

            case R.id.repairs:
                intent = new Intent(this, ActRepairs.class);
                startActivity(intent);
                break;

            case R.id.about:
                intent = new Intent(this,ActAbout.class);
                startActivity(intent);
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