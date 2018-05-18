package com.cioc.libreerp;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

/**
 * Created by admin on 30/04/18.
 */

class Backend {
//    static String serverUrl = "http://192.168.1.113:8000/";
    static String serverUrl = "http://192.168.1.108:8000/";
//    static String serverUrl = "https://vamso.cioc.in/";
    public Context context;

    SessionManager sessionManager;

    public Backend(Context context){
        this.context = context;
    }

    public AsyncHttpClient getHTTPClient(){
        sessionManager = new SessionManager(this.context);
        final String csrftoken = sessionManager.getCsrfId();
        final String sessionid = sessionManager.getSessionId();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-CSRFToken" , csrftoken);
        client.addHeader("COOKIE" , String.format("csrftoken=%s; sessionid=%s" ,csrftoken,  sessionid));
        return client;
    };

}
