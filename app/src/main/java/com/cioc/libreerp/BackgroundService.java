package com.cioc.libreerp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
import java.util.Map;
import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.interfaces.IWebSocket;
import io.crossbar.autobahn.websocket.types.WebSocketOptions;

import static android.content.ContentValues.TAG;


/**
 * Created by admin on 30/04/18.
 */

public class BackgroundService extends Service {
    public static final String ACTION = "com.cioc.libreerp.backendservice";
    SessionManager sessionManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("unsupported Operation");
    }

    @Override
    public void onCreate() {
        sessionManager = new SessionManager(this);
//        session = new Session();
//        session.addOnJoinListener(this::demonstrateSubscribe);
//        Client client = new Client(session, "ws://192.168.1.106:8080/ws", "default");
//        CompletableFuture<ExitInfo> exitInfoCompletableFuture = client.connect();
        start();
    }



    private final IWebSocket mConnection = new WebSocketConnection();

    private void start() {

        WebSocketOptions connectOptions = new WebSocketOptions();
        connectOptions.setReconnectInterval(5000);
        connectOptions.

        try {
            mConnection.connect("ws://192.168.1.106:8080/ws", new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {
                    Toast.makeText(BackgroundService.this, "Connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onMessage(String payload) {
                    Log.d(TAG, payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, reason);
                }
            }, connectOptions);
        } catch (WebSocketException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        intent = new Intent("com.cioc.libreerp.backendservice");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
        return START_STICKY;
    }
}
