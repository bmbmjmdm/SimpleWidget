package com.simplewidget;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.ListenableWorker;

public class BackgroundWorker extends Worker {
    private final Context context;

    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }
    @NonNull
    @Override
    public ListenableWorker.Result doWork() {

        Log.w("bg", "Worker do work====================");

        //Bundle extras = MainActivity.globalExtras;
        Intent service = new Intent(this.context, BackgroundHeadlessTaskService.class);
        //service.putExtras(extras);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.context.startForegroundService(service);
        } else {
            this.context.startService(service);
        }
        return ListenableWorker.Result.success();
    }
}