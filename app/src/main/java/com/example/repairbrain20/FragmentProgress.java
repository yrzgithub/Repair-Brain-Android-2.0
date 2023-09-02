package com.example.repairbrain20;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class FragmentProgress extends Fragment {

    TextView time_gone=null,lastly_relapse=null,next_step=null,pos_effect=null,neg_effect=null,hrs_left=null;
    EditText neg_edit=null,pos_edit=null,next_edit=null;
    Button effects = null, next = null;
    ProgressBar progress;
    ImageView loading;
    //MediaPlayer player;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        progress = view.findViewById(R.id.progress);
        progress.setMax(24);

        time_gone = view.findViewById(R.id.time_gone);
        lastly_relapse = view.findViewById(R.id.lastly_relapsed);
        next_step = view.findViewById(R.id.next_step);
        pos_effect = view.findViewById(R.id.pos_effect);
        neg_effect = view.findViewById(R.id.neg_effects);
        hrs_left = view.findViewById(R.id.hrs);

        TextView left_txt = view.findViewById(R.id.hrs_left);

        loading = view.findViewById(R.id.loading);
        Glide.with(getActivity())
                .load(R.drawable.loading_pink_list)
                .into(loading);

        lastly_relapse.setSelected(true);
        next_step.setSelected(true);
        pos_effect.setSelected(true);
        neg_effect.setSelected(true);


        DatabaseReference reference = User.getRepairReference();

        if(reference!=null)
        {
            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            if(!task.isSuccessful())
                            {
                                Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            DataSnapshot snapshot = task.getResult();

                            if(snapshot!=null && snapshot.exists())
                            {
                                ProgressData progress_data = snapshot.getValue(ProgressData.class);

                                String positive_effect = progress_data.getLastly_noted_positive_effects();
                                String negative_effect = progress_data.getLastly_noted_negative_effects();
                                String next_step_ = progress_data.getLastly_noted_next_steps();

                                ActRecovery.last_accuracy_percent = progress_data.getLast_accuracy_percent();

                                pos_effect.setText(positive_effect!=null? positive_effect : "...");
                                neg_effect.setText(negative_effect!=null? negative_effect : "...");
                                next_step.setText(next_step_!=null?next_step_ : "...");

                                LocalDateTime lastly_relapsed_object;

                                try
                                {
                                    Time time = progress_data.getLastly_relapsed();
                                    lastly_relapsed_object = LocalDateTime.of(time.getYear(),time.getMonth(),time.getDay(),time.getHour(),time.getMinute(),time.getSecond());
                                }
                                catch (Exception | Error e)
                                {
                                    time_gone.setText(R.string.not_found);
                                    lastly_relapse.setText(R.string.not_found);
                                    pos_effect.setText(R.string.not_found);
                                    next_step.setText(R.string.not_found);
                                    neg_effect.setText(R.string.not_found);
                                    return;
                                }

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
                                String lastly_relapsed_str = lastly_relapsed_object.format(formatter);

                                loading.setVisibility(View.INVISIBLE);
                                progress.setVisibility(View.VISIBLE);
                                hrs_left.setVisibility(View.VISIBLE);
                                left_txt.setVisibility(View.VISIBLE);

                                LocalDateTime now = LocalDateTime.now();
                                LocalDateTime finalLastly_relapsed_object = lastly_relapsed_object;

                                Duration duration = Duration.between(finalLastly_relapsed_object,now);

                                long hours = duration.toHours() % 24;

                                Log.e("sanjay",String.valueOf(hours));

                                int hrs = (int)hours;
                                progress.setProgress(hrs); // change
                                hrs_left.setText(String.format("%02d",24-hrs));

                                Handler handler = new Handler();
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

                                        int hrs = (int)hours;
                                        hrs_left.setText(String.format("%02d",24-hrs));

                                        String format = "%d days %d hrs %d mins %d secs";
                                        String time = String.format(format,days,hours,minutes,seconds);

                                        time_gone.setText(time);

                                        handler.postDelayed(this,1000);
                                    }
                                };

                                runnable.run();

                                lastly_relapse.setText(lastly_relapsed_str);

                                time_gone.setEnabled(true);
                            }
                        }
                    });
        }
    }
}