package com.example.repairbrain20;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class BootCompleted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("sanjay_boot", intent.getAction());

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            AppSettings settings = new AppSettings(context);

            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                settings.schedule_alarm();
            }
        }
    }
}
