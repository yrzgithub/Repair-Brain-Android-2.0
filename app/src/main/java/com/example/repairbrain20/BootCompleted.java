package com.example.repairbrain20;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class BootCompleted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            AppSettings settings = new AppSettings(context);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, settings.getHour());
            calendar.set(Calendar.MINUTE, settings.getMinute());

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent alarm_intent = new Intent(context, AlarmReceiver.class);
            PendingIntent alarm_pending = PendingIntent.getBroadcast(context, 100, alarm_intent, PendingIntent.FLAG_MUTABLE);

            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarm_pending);
        }
    }
}
