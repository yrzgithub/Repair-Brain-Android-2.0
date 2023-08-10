package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class HabitsAndAccuracy extends AppCompatActivity implements View.OnClickListener {

    static int last_accuracy_percent = 0;
    ViewPager pager;
    CheckNetwork network_check;
    ConnectivityManager cm;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habits_and_accuracy_pager);

        DrawerLayout drawer = findViewById(R.id.drawer);

        toggle = new ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close);
        toggle.syncState();

        drawer.addDrawerListener(toggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pager = findViewById(R.id.habits_pager);
        TabLayout tabs = findViewById(R.id.tabs);

      /*  network_check =  new CheckNetwork(this,pager);
        cm = (ConnectivityManager) getSystemService(ConnectivityManager.class); */

     /*   ConnectivityManager cm = (ConnectivityManager) getSystemService(ConnectivityManager.class);
        cm.registerDefaultNetworkCallback(new CheckNetwork(this,pager)); */

        TimeFragment accuracy = new TimeFragment();
        HabitsWindow habits = new HabitsWindow();

        TimeAndHabitsAdapter adapter = new TimeAndHabitsAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add_tab(accuracy,"Accuracy");
        adapter.add_tab(habits,"Habits");

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
        LinearLayout habits_ = findViewById(R.id.habits);
        LinearLayout effects = findViewById(R.id.effects);
        LinearLayout next_steps = findViewById(R.id.next_steps);
        LinearLayout about = findViewById(R.id.about);
        LinearLayout contact_developer = findViewById(R.id.contact_developer);

        home.setOnClickListener(this);
        time_gone.setOnClickListener(this);
        habits_.setOnClickListener(this);
        effects.setOnClickListener(this);
        next_steps.setOnClickListener(this);
        about.setOnClickListener(this);
        contact_developer.setOnClickListener(this);

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
        // cm.registerDefaultNetworkCallback(network_check);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // cm.unregisterNetworkCallback(network_check);
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
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;

            case R.id.time_gone:
                intent = new Intent(this,HabitsAndAccuracy.class);
                intent.putExtra("tab",0);
                startActivity(intent);
                break;

            case R.id.habits:
                intent = new Intent(this,HabitsAndAccuracy.class);
                intent.putExtra("tab",1);
                startActivity(intent);
                break;

            case R.id.effects:
                intent = new Intent(this,EffectsTabsAct.class);
                intent.putExtra("tab",0);
                startActivity(intent);
                break;

            case R.id.next_steps:
                intent = new Intent(this,EffectsTabsAct.class);
                intent.putExtra("tab",2);
                startActivity(intent);
                break;

            case R.id.about:
                break;

            case R.id.contact_developer:
                break;
        }
    }
}