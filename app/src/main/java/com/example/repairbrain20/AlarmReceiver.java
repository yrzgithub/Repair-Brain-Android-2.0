package com.example.repairbrain20;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    final String CHANNEL_NAME = "ask";
    final String ID = "ask";
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

      //  Toast.makeText(context,"received",Toast.LENGTH_LONG).show();

        NotificationManager manager = createChannel();

        Intent open = new Intent(context, ActLogin.class);
        PendingIntent open_pending = PendingIntent.getActivity(context,100,open,PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,ID)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setSmallIcon(R.drawable.icon_app)
                .setAutoCancel(true)
                .setContentText("Are you free?")
                .setContentIntent(open_pending)
                .setPriority(NotificationManager.IMPORTANCE_MAX);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

        managerCompat.notify(100, builder.build());

       // Toast.makeText(context,"Received",Toast.LENGTH_LONG).show();
    }

    public NotificationManager createChannel()
    {
        NotificationChannel channel = new NotificationChannel(ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = (NotificationManager) context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        return manager;
    }

}
