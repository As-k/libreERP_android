package in.cioc.trackingapp;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

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

/**
 * Created by Ashish on 17/09/18.
 */

public class BackgroundService extends Service {
    public static final String ACTION = "in.cioc.trackingapp.backendreceiver";
    public static final String TAG = "BackgroundService";
    Session session;
    public static final long INTERVAL = 1000 * 5;//variable to execute services every 10 second
    private Handler mHandler = new Handler(); // run on another Thread to avoid crash
    private Timer mTimer = null;// timer handling
    TimerTask timerTask;
    String mob;
    Client client;
    CompletableFuture<ExitInfo> exitInfoCompletableFuture;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("unsupported Operation");
    }

    @Override
    public void onCreate() {
        if (mTimer != null)
            mTimer.cancel();
        else
            mTimer = new Timer(); // recreate new timer
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, INTERVAL); // schedule task

    }

    private class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast at every 10 second
                    Boolean internetAvailable = false;
//                    Toast.makeText(getApplicationContext(), "service running" + client.toString() + exitInfoCompletableFuture.isDone() , Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), "Service running", Toast.LENGTH_SHORT).show();
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                    if (netInfo != null) {
                        internetAvailable = true;
                    }

                    if ((exitInfoCompletableFuture == null || exitInfoCompletableFuture.isDone()) && internetAvailable) {
                        session = new Session();
                        session.addOnJoinListener(this::demonstrateSubscribe);
                        client = new Client(session, "ws://wamp.cioc.in:8090/ws", "default");
                        exitInfoCompletableFuture = client.connect();
                    }
                }

                public void demonstrateSubscribe(Session session, SessionDetails details) {
//                    String usrname = sessionManager.getUsername();
//                    CompletableFuture<Subscription> subFuture = session.subscribe("service.self." + "agent",
//                            this::onEvent);
//                    subFuture.whenComplete((subscription, throwable) -> {
//                        if (throwable == null) {
//                            System.out.println("Subscribed to topic " + subscription.topic);
                            Toast.makeText(getApplicationContext(), "wamp server connected", Toast.LENGTH_SHORT).show();
//
//                        } else {
//                            throwable.printStackTrace();
//                        }
//                    });
                }

                private void onEvent(List<Object> args, Map<String, Object> kwargs, EventDetails details) {
                    System.out.println(String.format("Got event: %s", args.get(0)));
                    Toast.makeText(BackgroundService.this, args.toString(), Toast.LENGTH_SHORT).show();
                    // add a notification strip here
                }
            });


            CallBarring.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    Toast.makeText(BackgroundService.this, "received"+messageText, Toast.LENGTH_SHORT).show();
                }
            });

            BackgroundReceiver.sentSMS(new SmsSendingListener() {
                @Override
                public void smsSending(String msg) {
                    Toast.makeText(BackgroundService.this, "self"+msg, Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        //create a intent that you want to start again..
//        String manufacturer = "xiaomi";
//        if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
//            //this will open auto start screen where user can enable permission for your app
//            Intent intent = new Intent();
//            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
//            PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
//        }
//        super.onTaskRemoved(rootIntent);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "destroy", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ACTION);
        sendBroadcast(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent.getExtras()!=null) {
            mob = intent.getExtras().getString("mob");
            Toast.makeText(this, "service:  " + mob, Toast.LENGTH_SHORT).show();
        }
        Intent intent1 = new Intent(ACTION);
        sendBroadcast(intent1);
        return START_STICKY;
    }
}
