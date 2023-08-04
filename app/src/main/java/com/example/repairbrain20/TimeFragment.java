package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class TimeFragment extends Fragment {

    TextView time_gone=null,lastly_relapse=null,next_step=null,pos_effect=null,neg_effect=null,hrs_left=null;
    EditText neg_edit=null,pos_edit=null,next_edit=null;
    Button effects = null, next = null;
    ProgressBar progress;
    //MediaPlayer player;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        time_gone = view.findViewById(R.id.time_gone);
        lastly_relapse = view.findViewById(R.id.lastly_relapsed);
        next_step = view.findViewById(R.id.next_step);
        pos_effect = view.findViewById(R.id.pos_effect);
        neg_effect = view.findViewById(R.id.neg_effects);
        hrs_left = view.findViewById(R.id.hrs);

        lastly_relapse.setSelected(true);
        next_step.setSelected(true);
        pos_effect.setSelected(true);
        neg_effect.setSelected(true);

        // effects = view.findViewById(R.id.effects);
        next = view.findViewById(R.id.next);

        progress = view.findViewById(R.id.progress);

        LinearLayout root = view.findViewById(R.id.root);

        // player = MediaPlayer.create(this,R.raw.ticksound);
        // player.setLooping(true);

        progress.setMax(24);

        User.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData user_data = snapshot.getValue(UserData.class);

                Log.e("sanjay_snap",user_data.toString());

                next_step.setText(user_data.getLastly_noted_next_steps());
                pos_effect.setText(user_data.getLastly_noted_positive_effects());
                neg_effect.setText(user_data.getLastly_noted_negative_effects());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("sanjay_snap",error.getMessage());
            }
        });

        DatabaseReference lastly_relapsed_reference = User.getReference().child("lastly_relapsed");

        lastly_relapsed_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot!=null && snapshot.exists())
                {
                    LocalDateTime lastly_relapsed_object;

                    try
                    {
                        Time time = snapshot.getValue(Time.class);
                        lastly_relapsed_object = LocalDateTime.of(time.getYear(),time.getMonth(),time.getDay(),time.getHour(),time.getMinute(),time.getSecond());
                    }
                    catch (Exception | Error e)
                    {
                        Log.e("sanjay_timef",e.getMessage());
                        lastly_relapsed_object = null;
                    }

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
                    LocalDateTime finalLastly_relapsed_object = lastly_relapsed_object;
                    Runnable runnable = new Runnable() {
                        @SuppressLint("DefaultLocale")
                        @Override
                        public void run() {
                            LocalDateTime now = LocalDateTime.now();

                            Duration duration = Duration.between(finalLastly_relapsed_object,now);

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
                Intent intent = new Intent(getActivity(),EffectsTabsAct.class);
                startActivity(intent);
            }
        });

      /*  effects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(),EffectsTabsAct.class);
                intent.putExtra("effect",0);
                startActivity(intent);
            }
        });
       */

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
            assert map!=null;
        }
        catch (Exception | Error e)
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

}