package com.cioc.libreerp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FlashActivity extends AppCompatActivity {

    private Handler hd = new Handler();
    SessionManager sessionManager;
    boolean res; //, loc;

    String STATUS = "status";
    String csrfId, sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        sessionManager = new SessionManager(this);
        getSupportActionBar().hide();
        stopService(new Intent(this, BackgroundService.class));
        stopService(new Intent(this, LocationService.class));

        res = sessionManager.getStatus();
        csrfId = sessionManager.getCsrfId();
        sessionId = sessionManager.getSessionId();

        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
//                loc = csrfId.equals("") && sessionId.equals("");
                if (csrfId.equals("") && sessionId.equals("")) {
//                        if (res) {
//                            startActivity(new Intent(FlashActivity.this, MainActivity.class));
//                        } else {
                        startActivity(new Intent(FlashActivity.this, LoginActivity.class));
//                        sessionManager.setStatus(false);
//                        }
                } else {
                    startActivity(new Intent(FlashActivity.this, MainActivity.class));
//                    sessionManager.setStatus(true);
                }
                finish();
            }
        },3000);
    }


    public boolean isRes() {
        return res;
    }

}
