package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                            String format = "%d days %d hrs %d mins %d secs";
                            String time_ = String.format(format,days,hours,minutes,seconds);

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
                            String date = formatter.format(now);

                            Relapse relapse = new Relapse(date,time_);

                            reference.child("relapses").push().setValue(relapse);

                            DatabaseReference count_reference = reference.child("relapsed_count");
                            count_reference
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            Integer count = 0;

                                            if(task.isSuccessful())
                                            {
                                                count = task.getResult().getValue(Integer.class);
                                                if(count==null)
                                                {
                                                    count = 1;
                                                }
                                                else
                                                {
                                                    count+=1;
                                                }

                                                count_reference.setValue(count);

                                                startActivity(new Intent(ActHome.this,ActJourney.class));

                                                Toast.makeText(ActHome.this, count +" times relapsed",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    });

                    reference.child("lastly_relapsed").setValue(new Time(LocalDateTime.now()))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Toast.makeText(ActHome.this,"Starting time rested",Toast.LENGTH_LONG).show();
                                }
                            });

                    intent.putExtra("free",false);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings)
        {
            startActivity(new Intent(ActHome.this, ActSettings.class));
        }
        return super.onOptionsItemSelected(item);
    }
}