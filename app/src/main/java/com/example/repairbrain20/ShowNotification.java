package com.example.repairbrain20;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ShowNotification {

    Context context;

    ShowNotification(Context context)
    {
        this.context = context;

        Intent intent = new Intent(context,AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
