package com.example.repairbrain20;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AppSettings settings = new AppSettings(context);
            settings.schedule_alarm();
        }
    }
}
