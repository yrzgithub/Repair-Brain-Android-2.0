package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class HabitsAndAccuracy extends AppCompatActivity {

    static int last_accuracy_percent = 0;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habits_and_accuracy_pager);

        DatabaseReference reference =  User.getReference();

        if (reference!=null)
        {
            reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    UserData data =  task.getResult().getValue(UserData.class);
                    last_accuracy_percent = data.getLast_accuracy_percent();
                }
            });
        }

        pager = findViewById(R.id.habits_pager);
        TabLayout tabs = findViewById(R.id.tabs);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(ConnectivityManager.class);
        cm.registerDefaultNetworkCallback(new CheckNetwork(this,pager));

        TimeFragment accuracy = new TimeFragment();
        HabitsWindow habits = new HabitsWindow();

        TimeAndHabitsAdapter adapter = new TimeAndHabitsAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add_tab(accuracy,"Accuracy");
        adapter.add_tab(habits,"Habits");

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        User.getReference().child("last_accuracy_percent").setValue(HabitsAdapter.current_percent);
        super.onStop();
    }
}