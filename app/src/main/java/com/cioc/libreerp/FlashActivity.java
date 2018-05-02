package com.cioc.libreerp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FlashActivity extends AppCompatActivity {

    private Handler hd = new Handler();
    SessionManager sessionManager;
    boolean res;

    String STATUS = "status";
    String csrfId, sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        sessionManager = new SessionManager(this);
        getSupportActionBar().hide();

        res = sessionManager.getStatus();
        csrfId = sessionManager.getCsrfId();
        sessionId = sessionManager.getSessionId();

        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (csrfId.equals("") && sessionId.equals("")) {
//                        if (res) {
//                            startActivity(new Intent(FlashActivity.this, MainActivity.class));
//                        } else {
                            startActivity(new Intent(FlashActivity.this, LoginActivity.class));
//                        }
                    }else {
                        startActivity(new Intent(FlashActivity.this, MainActivity.class));
                    }
                finish();
            }
        },3000);
    }


    public boolean isRes() {
        return res;
    }

}
