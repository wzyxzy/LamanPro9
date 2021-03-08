package com.wzy.lamanpro.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wzy.lamanpro.activity.FirstActivity;

public class MyReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        if (intent.getAction().equals(ACTION)) {
            Intent newIntent = new Intent(context, FirstActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }
    }
}