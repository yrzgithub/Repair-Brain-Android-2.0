package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeActivity extends AppCompatActivity {

    TextView time_gone=null,lastly_relapse=null,next_step=null,pos_effect=null,neg_effect=null,hrs_left=null;
    EditText neg_edit=null,pos_edit=null,next_edit=null;
    FirebaseDatabase database;
    Button effects = null, next = null;
    DatabaseReference reference;
    ProgressBar progress;
    //MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        //TextView
        time_gone = findViewById(R.id.time_gone);
        lastly_relapse = findViewById(R.id.lastly_relapsed);
        next_step = findViewById(R.id.next_step);
        pos_effect = findViewById(R.id.pos_effect);
        neg_effect = findViewById(R.id.neg_effects);
        hrs_left = findViewById(R.id.hrs);

        lastly_relapse.setSelected(true);
        next_step.setSelected(true);
        pos_effect.setSelected(true);
        neg_effect.setSelected(true);

       // effects = findViewById(R.id.effects);
        next = findViewById(R.id.next);

        progress = findViewById(R.id.progress);

        LinearLayout root = findViewById(R.id.root);

       // player = MediaPlayer.create(this,R.raw.ticksound);
        // player.setLooping(true);

        progress.setMax(24);

        String uid = User.getUid();

        database = FirebaseDatabase.getInstance();

        reference = database.getReference(uid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData user_data = snapshot.getValue(UserData.class);

                Log.e("sanjay_snap",user_data.getNext_step());

                next_step.setText(user_data.getNext_step());
                pos_effect.setText(user_data.getLastly_noted_change());
                neg_effect.setText(user_data.getLastly_noted_side_effect());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("sanjay_snap",error.getMessage());
            }
        });

        DatabaseReference lastly_relapsed_reference = reference.child("lastly_relapsed");

        lastly_relapsed_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot!=null && snapshot.exists())
                {
                    LocalDateTime lastly_relapsed_object = snapshot.getValue(Time.class).localtime();

                    if(lastly_relapsed_object==null)
                    {
                        time_gone.setText(R.string.not_found);
                        lastly_relapse.setText(R.string.not_found);
                        next_step.setText(R.string.not_found);
                        pos_effect.setText(R.string.not_found);
                        neg_effect.setText(R.string.not_found);
                        return;
                    }

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E,MMM dd yyyy");
                    String lastly_relapsed_str = lastly_relapsed_object.format(formatter);

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @SuppressLint("DefaultLocale")
                        @Override
                        public void run() {
                            LocalDateTime now = LocalDateTime.now();

                            Duration duration = Duration.between(lastly_relapsed_object,now);

                            long days = duration.toDays();
                            long hours = duration.toHours() % 24;
                            long minutes = duration.toMinutes() % 60;
                            long seconds = duration.getSeconds() % 60;

                            Log.e("sanjay",String.valueOf(hours));

                            int hrs = (int)hours;
                            progress.setProgress(hrs);
                            hrs_left.setText(String.format("%02d",24-hrs));

                            String format = "%d days %d hrs %d mins %d secs";
                            String time = String.format(format,days,hours,minutes,seconds);
                            Log.e("sanjay_uruttu",time);
                            time_gone.setText(time);

                            handler.postDelayed(this,1000);
                        }
                    };

                    runnable.run();

                    lastly_relapse.setText(lastly_relapsed_str);

                    time_gone.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TimeActivity.this,Habits.class);
                startActivity(intent);
            }
        });

        effects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TimeActivity.this,EffectsTabsAct.class);
                intent.putExtra("effect",0);
                startActivity(intent);
            }
        });

        root.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               // player.pause();
            }
        });

    }

    public LocalDateTime getLocalDateTime(Map<String,Object> data,String key)
    {

        Map<String,Object> map;

        try {
            map = (Map<String, Object>) data.get(key);
        }
        catch (Exception e)
        {
            return null;
        }

        int year = Integer.valueOf(map.get("year").toString());
        int month = Integer.parseInt(map.get("month").toString());
        int day = Integer.parseInt(map.get("day").toString());
        int hour = Integer.parseInt(map.get("hour").toString());
        int minute = Integer.parseInt(map.get("minute").toString());
        int second = Integer.parseInt(map.get("second").toString());

        return LocalDateTime.of(year,month,day,hour,minute,second);
    }

    public void delete_player()
    {
        /*
        if(player!=null)
        {
            player.pause();
            player.release();
            player = null;
        }
         */
    }

    @Override
    protected void onStart() {
       // player.start();
        super.onStart();
    }

    @Override
    protected void onStop() {
        //player.pause();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Toast.makeText(this,"toast",Toast.LENGTH_LONG).show();
        //delete_player();
        super.onDestroy();
    }
}