package com.example.repairbrain20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Permission;
import java.util.Calendar;

public class ActSettings extends AppCompatActivity {

    RelativeLayout auto_login,notification;
    SwitchCompat auto_login_switch,notification_switch;
    TextView delete,time;
    LinearLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        AppSettings settings = new AppSettings(this);

        main = findViewById(R.id.main);

        auto_login = findViewById(R.id.login_rel);
        notification = findViewById(R.id.notification_rel);

        time = findViewById(R.id.notify_time_txt);

        auto_login_switch = findViewById(R.id.auto_login_switch);
        notification_switch = findViewById(R.id.notify_switch);

        delete = findViewById(R.id.del_acc);

        auto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_login_switch.setChecked(!auto_login_switch.isChecked());
            }
        });

        auto_login_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.setAuto_login(b);
            }
        });

        notification_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b && ActivityCompat.checkSelfPermission(ActSettings.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(ActSettings.this,"Allow Notifications",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package",getPackageName(),null));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                settings.setShow_notification(b);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar snackbar = Snackbar.make(main,"Deleting Account", BaseTransientBottomBar.LENGTH_INDEFINITE);
                snackbar.show();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user!=null)
                {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                snackbar.dismiss();
                                Toast.makeText(ActSettings.this,"Account Deleted",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ActSettings.this,ActLogin.class));
                            }
                        }
                    });
                }
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View view) {

                Toast.makeText(ActSettings.this,"clicked",Toast.LENGTH_SHORT).show();;

                Calendar calender = Calendar.getInstance();
                int hour = calender.get(Calendar.HOUR_OF_DAY);
                int minute = calender.get(Calendar.MINUTE);

                new TimePickerDialog(ActSettings.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        settings.setHour(hour);
                        settings.setMinute(minute);
                    }
                },hour,minute,true).show();
            }
        });
    }
}