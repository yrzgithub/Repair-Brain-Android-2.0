package com.example.repairbrain20;

import static com.example.repairbrain20.AdapterRepairsList.add_link;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

public class ActSettings extends AppCompatActivity {

    RelativeLayout time_rel;
    SwitchCompat auto_login_switch, notification_switch;
    ImageView set_link;
    TextView time;
    ImageView delete;
    LinearLayout main;
    CheckNetwork net;
    int NOTIFICATION_REQUEST = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        AppSettings settings = new AppSettings(this);

        main = findViewById(R.id.main);

        net = new CheckNetwork(this, main);

        time_rel = findViewById(R.id.time_rel);

        time = findViewById(R.id.notify_time_txt);

        auto_login_switch = findViewById(R.id.auto_login_switch);
        notification_switch = findViewById(R.id.notify_switch);

        delete = findViewById(R.id.del_acc);
        set_link = findViewById(R.id.set_link);

        auto_login_switch.setChecked(settings.isAuto_login());
        notification_switch.setChecked(settings.isShow_notification());

        setTime(settings);

        set_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference playlist_reference = User.getPlaylistReference();
                add_link(ActSettings.this, playlist_reference);
            }
        });

        auto_login_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.setAuto_login(b);
            }
        });

        notification_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    settings.cancel_alarm();
                }

                if (b && ActivityCompat.checkSelfPermission(ActSettings.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_REQUEST);
                    } else {
                        Toast.makeText(ActSettings.this, "Allow notifications", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse(getPackageName()));
                        startActivity(intent);
                    }
                }
                settings.setShow_notification(b);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!CheckNetwork.isAvailable(ActSettings.this)) {
                    Toast.makeText(ActSettings.this, "Network not Available", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(ActSettings.this)
                        .setIcon(R.drawable.icon_app)
                        .setTitle(R.string.app_name)
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Snackbar snackbar = Snackbar.make(main, "Deleting Account", BaseTransientBottomBar.LENGTH_INDEFINITE);
                                snackbar.show();

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                if (user != null) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                snackbar.dismiss();
                                                Toast.makeText(ActSettings.this, "Account Deleted", Toast.LENGTH_LONG).show();
                                                settings.setAuto_login(false);
                                                SharedPreferences preferences = ActSettings.this.getSharedPreferences("login_data", MODE_PRIVATE);
                                                preferences.edit().clear().apply();
                                                startActivity(new Intent(ActSettings.this, ActLogin.class));
                                            }
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        time_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calender = Calendar.getInstance();
                int hour = calender.get(Calendar.HOUR_OF_DAY);
                int minute = calender.get(Calendar.MINUTE);

                new TimePickerDialog(ActSettings.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        settings.setHour(hour);
                        settings.setMinute(minute);
                        setTime(settings);

                        settings.schedule_alarm();
                    }
                }, hour, minute, false).show();
            }
        });
    }

    public void setTime(AppSettings settings) {
        int hour = settings.getHour();
        int minute = settings.getMinute();

        String time;

        if (hour > 12) {
            time = String.format("%02d:%02d %s", hour - 12, minute, "PM");
        } else {
            time = String.format("%02d:%02d %s", hour, minute, "AM");
        }
        this.time.setText(time);
    }

    @Override
    protected void onPause() {
        net.unregister();
        super.onPause();
    }

    @Override
    protected void onResume() {
        net.register();
        super.onResume();
    }
}