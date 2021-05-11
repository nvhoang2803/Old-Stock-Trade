package com.example.oldstocktrade.Notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class Notification extends ContextWrapper {
    private static final String ID = "some_id";
    private static final String NAME = "FirebaseAPP";

    private NotificationManager notificationManager;

    public Notification(Context base){
        super(base);
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            createChannel();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    NotificationManager getManager() {
        if (notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public android.app.Notification.Builder getONotifications(String title, String body, PendingIntent pendingIntent, Uri soundUri, String icon){
        return new android.app.Notification.Builder(getApplicationContext(), ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));
    }
}
