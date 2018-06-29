package com.cioc.libreerp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.EventDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;
import io.crossbar.autobahn.wamp.types.TransportOptions;

/**
 * Created by admin on 30/04/18.
 */

public class BackgroundService extends Service {
    public static final String ACTION = "com.cioc.libreerp.backendservice";
    Session session;
    SessionManager sessionManager;
    public static final long INTERVAL = 1000 * 5;//variable to execute services every 10 second
    private Handler mHandler = new Handler(); // run on another Thread to avoid crash
    private Timer mTimer = null;// timer handling
    TimerTask timerTask;
    //    boolean internetAvailable;
    Client client;
    CompletableFuture<ExitInfo> exitInfoCompletableFuture;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("unsupported Operation");
    }

    @Override
    public void onCreate() {
        sessionManager = new SessionManager(this);

        if (mTimer != null)
            mTimer.cancel();
        else
            mTimer = new Timer(); // recreate new timer
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, INTERVAL); // schedule task
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
                    Boolean internetAvailable = false;
                    //Toast.makeText(getApplicationContext(), "service running" + client.toString() + exitInfoCompletableFuture.isDone() , Toast.LENGTH_SHORT).show();
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                    if (netInfo != null) {
                        internetAvailable = true;
                    }

                    if ((exitInfoCompletableFuture == null || exitInfoCompletableFuture.isDone()) && internetAvailable) {
                        session = new Session();
                        session.addOnJoinListener(this::demonstrateSubscribe);
                        client = new Client(session, "ws://192.168.1.106:8080/ws", "default");
                        exitInfoCompletableFuture = client.connect();
                    }
                }


                public void demonstrateSubscribe(Session session, SessionDetails details) {

                    String usrname = sessionManager.getUsername();

                    CompletableFuture<Subscription> subFuture = session.subscribe("service.self." + usrname,
                            this::onEvent);
                    subFuture.whenComplete((subscription, throwable) -> {
                        if (throwable == null) {
                            System.out.println("Subscribed to topic " + subscription.topic);
                            Toast.makeText(getApplicationContext(), "Subscribed", Toast.LENGTH_SHORT).show();
                        } else {
                            throwable.printStackTrace();
                        }
                    });
                }

                private void onEvent(List<Object> args, Map<String, Object> kwargs, EventDetails details) {
                    System.out.println(String.format("Got event: %s", args.get(0)));

                    if (args.get(0).equals("call")) {

                        Toast.makeText(getApplicationContext(), "Make a call" + args.get(1), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_CALL);

                        intent.setData(Uri.parse("tel:" + args.get(1)));

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        getApplicationContext().startActivity(intent);

                    }else if (args.get(0).equals("endcall")){
                        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        try {
                            Class c = Class.forName(tm.getClass().getName());
                            Method m = c.getDeclaredMethod("getITelephony");
                            m.setAccessible(true);
                            Object telephonyService = m.invoke(tm);

                            c = Class.forName(telephonyService.getClass().getName());
                            m = c.getDeclaredMethod("endCall");
                            m.setAccessible(true);
                            m.invoke(telephonyService);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else if (args.get(0).equals("sms")){
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(args.get(1).toString(), null, args.get(2).toString(), null, null);
                            Toast.makeText(getApplicationContext(), "Message Sent",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    }



    @Override
    public void onDestroy() {
//        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        intent = new Intent("com.cioc.libreerp.backendservice");
        sendBroadcast(intent);
        return START_STICKY;
    }
}
