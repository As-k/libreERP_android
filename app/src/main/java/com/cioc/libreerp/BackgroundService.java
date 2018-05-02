package com.cioc.libreerp;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin on 30/04/18.
 */

public class BackgroundService extends Service {
    public static final long INTERVAL = 5000;//variable to execute services every 10 second
    private Handler mHandler = new Handler(); // run on another Thread to avoid crash
    private Timer mTimer = null;// timer handling
    TimerTask timerTask;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
//    public BackgroundService(String name) {
//        super(name);
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("unsupported Operation");
    }

//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//        WakefulBroadcastReceiver.completeWakefulIntent(intent);
//    }

    @Override
    public void onCreate() {
        // cancel if service is  already existed
        if(mTimer!=null)
            mTimer.cancel();
        else
            mTimer=new Timer(); // recreate new timer
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(),0,INTERVAL);// schedule task
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "In Destroy", Toast.LENGTH_SHORT).show();//display toast when method called
        try {
            mTimer.cancel();
            timerTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent("com.cioc.libreerp.backendservice");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }


    //inner class of TimeDisplayTimerTask
    private class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast at every 10 second
                    Toast.makeText(getApplicationContext(), "service running", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
//        boolean res = sessionManager.getStatus();
//        if (res) {
            intent = new Intent("com.cioc.libreerp.backendservice");
            intent.putExtra("yourvalue", "torestore");
            sendBroadcast(intent);
            return START_STICKY;
//        }
//        else return START_STICKY_COMPATIBILITY;
    }
}
