package com.example.repairbrain20;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    final String CHANNEL_NAME = "ask";
    final String ID = "ask";
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        AppSettings settings = new AppSettings(context);

        if (settings.isShow_notification() && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            createChannel();

            Intent open = new Intent(context, ActLogin.class);
            PendingIntent open_pending = PendingIntent.getActivity(context, 100, open, PendingIntent.FLAG_MUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setSmallIcon(R.drawable.icon_app)
                    .setAutoCancel(true)
                    .setContentText("Are you free?")
                    .setContentIntent(open_pending)
                    .setPriority(NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

            managerCompat.notify(2002, builder.build());
        }
    }

    public void createChannel() {
        NotificationChannel channel = new NotificationChannel(ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = (NotificationManager) context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

}
