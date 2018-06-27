package com.cioc.libreerp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.EventDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;

/**
 * Created by admin on 30/04/18.
 */

public class BackgroundBroadcastReceiver extends BroadcastReceiver {
    SessionManager sessionManager;
    Context ctx;
    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        sessionManager = new SessionManager(context);
        if (sessionManager.getCsrfId() != "" && sessionManager.getSessionId() != "") {
            context.startService(new Intent(context, BackgroundService.class));
        }
    }
}