package com.example.repairbrain20;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActRecovery extends AppCompatActivity implements View.OnClickListener {

    static int last_accuracy_percent = 0;
    ViewPager pager;
    CheckNetwork network_check;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        drawer = findViewById(R.id.drawer);

        toggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        toggle.syncState();

        drawer.addDrawerListener(toggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pager = findViewById(R.id.habits_pager);
        TabLayout tabs = findViewById(R.id.tabs);

        network_check = new CheckNetwork(this, pager);

        FragmentProgress progress = new FragmentProgress();
        FragmentTriggers triggers = new FragmentTriggers();
        FragmentPractices practices = new FragmentPractices();

        AdapterTimeAndHabits adapter = new AdapterTimeAndHabits(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.add_tab(progress, "Progress");
        adapter.add_tab(triggers, "Triggers");
        adapter.add_tab(practices, "Practices");

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        LinearLayout home = findViewById(R.id.home);
        LinearLayout recovery = findViewById(R.id.recovery);
        LinearLayout effects = findViewById(R.id.effects);
        LinearLayout journey = findViewById(R.id.journey);
        LinearLayout update = findViewById(R.id.update);
        LinearLayout about = findViewById(R.id.about);
        LinearLayout contact_developer = findViewById(R.id.contact_developer);
        LinearLayout logout = findViewById(R.id.logout);


        home.setOnClickListener(this);
        recovery.setOnClickListener(this);
        effects.setOnClickListener(this);
        journey.setOnClickListener(this);
        update.setOnClickListener(this);
        about.setOnClickListener(this);
        contact_developer.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.home:
                intent = new Intent(this, ActHome.class);
                startActivity(intent);
                break;

            case R.id.recovery:
                intent = new Intent(this, ActRecovery.class);
                startActivity(intent);
                break;

            case R.id.effects:
                intent = new Intent(this, ActEvolution.class);
                startActivity(intent);
                break;

            case R.id.journey:
                intent = new Intent(this, ActJourney.class);
                startActivity(intent);
                break;

            case R.id.update:
                if (drawer.isDrawerOpen(Gravity.RIGHT)) drawer.closeDrawer(Gravity.RIGHT, true);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                ProgressDialog progress = new ProgressDialog(this);
                progress.setCanceledOnTouchOutside(false);
                progress.setOnCancelListener(null);
                progress.setMessage("Checking");
                progress.show();

                reference
                        .child("versions")
                        .child("latest_version")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                progress.dismiss();

                                if (task.isSuccessful()) {
                                    Version version = task.getResult().getValue(Version.class);
                                    try {
                                        float latest_version = Float.parseFloat(version.getName());
                                        if (Data.CURRENT_VERSION < latest_version) {
                                            new AlertDialog.Builder(ActRecovery.this)
                                                    .setTitle(R.string.app_name)
                                                    .setIcon(R.drawable.icon_app)
                                                    .setMessage("Update Available")
                                                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            open_github();
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", null)
                                                    .show();
                                        } else {
                                            Toast.makeText(ActRecovery.this, "Already in Latest Version", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(ActRecovery.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                break;

            case R.id.about:
                open_github();
                break;

            case R.id.contact_developer:
                startActivity(new Intent(this, ActContactDeveloper.class));
                break;

            case R.id.logout:
                SharedPreferences preferences = getSharedPreferences("login_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().apply();

                FirebaseAuth.getInstance().signOut();

                intent = new Intent(this, ActLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }

    public void open_github() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/yrzgithub/Repair-Brain-Android-3.0"));
        startActivity(intent);
    }
}