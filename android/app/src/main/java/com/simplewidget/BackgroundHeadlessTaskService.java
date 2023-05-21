package com.simplewidget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.facebook.react.bridge.WritableMap;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

public class BackgroundHeadlessTaskService extends HeadlessJsTaskService {
    @Override
    protected @Nullable
    HeadlessJsTaskConfig getTaskConfig(Intent intent) {
        Log.w("bg", "Headless doing something====================");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.w("bg", "1====================");
            createChannel();
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), "demo")
                    .setContentTitle("Headless Work")
                    .setTicker("runn")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .build();
            startForeground(1, notification);
        }

        Bundle extras = intent.getExtras();
        WritableMap map;
        if (extras == null) {
            map = Arguments.createMap();
        }
        else {
            map = Arguments.fromBundle(extras);
        }
        Log.w("bg", "2====================");
        return new HeadlessJsTaskConfig(
                "backgroundTask",
                map,
                5000,
                true
        );
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        String description = "test channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("demo", "test", importance);
        channel.setDescription(description);
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(channel);

    }
}