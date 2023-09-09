package com.example.repairbrain20;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActHome extends AppCompatActivity {

    ImageButton free_button = null, hand_cuffed_button = null;
    TextView ask;
    AppSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ask = findViewById(R.id.ask);

        free_button = findViewById(R.id.free_image);
        hand_cuffed_button = findViewById(R.id.hand_cuffed_image);

        settings = new AppSettings(this);
        boolean yes_or_now = settings.isYes_or_no();

        if (yes_or_now) {
            free_button.setImageResource(R.drawable.yes);
            hand_cuffed_button.setImageResource(R.drawable.no);
        }

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.image_button_anim);

        free_button.startAnimation(animation);
        hand_cuffed_button.startAnimation(animation);

        Intent intent = new Intent(ActHome.this, ActRecovery.class);

        free_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("free", true);
                startActivity(intent);
            }
        });

        hand_cuffed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference reference = User.getRepairReference();

                if (reference != null) {
                    reference.child("lastly_relapsed").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Time time = task.getResult().getValue(Time.class);
                            LocalDateTime localDateTime = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDay(), time.getHour(), time.getMinute(), time.getSecond());

                            LocalDateTime now = LocalDateTime.now();

                            Duration duration = Duration.between(localDateTime, now);

                            long days = duration.toDays();
                            long hours = duration.toHours() % 24;
                            long minutes = duration.toMinutes() % 60;
                            long seconds = duration.getSeconds() % 60;

                            String format = "%d days %d hrs %d mins %d secs";
                            String time_ = String.format(format, days, hours, minutes, seconds);

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
                            String date = formatter.format(now);

                            Relapse relapse = new Relapse(date, time_);

                            reference.child("relapses").push().setValue(relapse);

                            DatabaseReference count_reference = reference.child("days_difference");
                            count_reference
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                                            if (task.isSuccessful()) {
                                                DaysDifference difference = task.getResult().getValue(DaysDifference.class);

                                                long diff = difference.getDifference();
                                                int count = difference.getCount();

                                                count_reference.setValue(new DaysDifference(difference));

                                                startActivity(new Intent(ActHome.this, ActJourney.class));

                                                Toast.makeText(ActHome.this, count + " times relapsed in " + diff + " days", Toast.LENGTH_LONG).show();
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

                    intent.putExtra("free", false);
                    startActivity(intent);
                }
            }
        });

        DatabaseReference reference = User.getRepairReference();
        if (reference != null) {
            reference
                    .child("ask")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String title = snapshot.getValue(String.class);
                            if (title != null) {
                                ask.setText(title);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(ActHome.this, ActSettings.class));
                break;

            case R.id.change:
                DatabaseReference reference = User.getRepairReference();

                View view = getLayoutInflater().inflate(R.layout.alert_ask_question, null);
                EditText question = view.findViewById(R.id.question);

                if (reference != null) {
                    new AlertDialog.Builder(this)
                            .setView(view)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String ques = question.getText().toString().trim();

                                    if (ques.isEmpty()) {
                                        Toast.makeText(ActHome.this, "Invalid Question", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (!ques.endsWith("?")) {
                                        ques += "?";
                                    }

                                    reference
                                            .child("ask")
                                            .setValue(ques);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }
                break;
        }

        return true;
    }
}