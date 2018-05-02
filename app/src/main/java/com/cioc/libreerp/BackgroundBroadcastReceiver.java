package com.cioc.libreerp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by admin on 30/04/18.
 */

public class BackgroundBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(BackgroundBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        Intent startServiceIntent = new Intent(context, BackgroundBroadcastReceiver.class);
        context.startService(startServiceIntent);
    }
}