package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.Executor;

public class ActHome extends AppCompatActivity {

    ImageButton free_button = null,hand_cuffed_button = null;
    CheckNetwork network_check;
    ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout main = findViewById(R.id.main);
       /* network_check =  new CheckNetwork(this,main);
        cm = (ConnectivityManager) getSystemService(ConnectivityManager.class); */

        free_button = findViewById(R.id.free_image);
        hand_cuffed_button = findViewById(R.id.hand_cuffed_image);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.image_button_anim);

        free_button.startAnimation(animation);
        hand_cuffed_button.startAnimation(animation);

        Intent intent = new Intent(ActHome.this, ActRecovery.class);

        free_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("free",true);
                startActivity(intent);
            }
        });

        hand_cuffed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference reference = User.getRepairReference();

                if(reference!=null)
                {
                    reference.child("lastly_relapsed").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Time time = task.getResult().getValue(Time.class);
                            LocalDateTime localDateTime = LocalDateTime.of(time.getYear(),time.getMonth(),time.getDay(),time.getHour(),time.getMinute(),time.getSecond());

                            LocalDateTime now = LocalDateTime.now();

                            Duration duration =  Duration.between(localDateTime,now);

                            long days = duration.toDays();
                            long hours = duration.toHours() % 24;
                            long minutes = duration.toMinutes() % 60;
                            long seconds = duration.getSeconds() % 60;

                            Log.e("sanjay",String.valueOf(hours));

                            String format = "%d days %d hrs %d mins %d secs";
                            String time_ = String.format(format,days,hours,minutes,seconds);

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E,MMM dd yyyy");
                            String date = formatter.format(now);

                            reference.child("relapses").child(date).setValue(time_);
                        }
                    });

                    reference.child("lastly_relapsed").setValue(new Time(LocalDateTime.now()))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ActHome.this,"Starting time rested",Toast.LENGTH_LONG).show();
                                }
                            });

                    intent.putExtra("free",false);
                    startActivity(intent);
                }
            }
        });

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
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // getMenuInflater().inflate(R.menu.fragment_add_menu,menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}