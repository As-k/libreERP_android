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
    static String serverUrl = "http://192.168.1.113:8000/";
    static Context context;

    static SessionManager sessionManager;

    public Backend(Context context){
        this.context = context;
    }

    public static AsyncHttpClient getHTTPClient(Context context){
        // reading the existing keys
        sessionManager = new SessionManager(context);
        final String csrftoken = sessionManager.getCsrfId();
        final String sessionid = sessionManager.getSessionId();

        CookieStore httpCookieStoreSt = new PersistentCookieStore(context);
        httpCookieStoreSt.clear();
        AsyncHttpClient client = new AsyncHttpClient();

        String slimedUrl = serverUrl.replace("http://", "").replace("https://", "");

        BasicClientCookie newCsrftokenCookie = new BasicClientCookie("csrftoken", csrftoken);
        newCsrftokenCookie.setVersion(1);
        newCsrftokenCookie.setDomain(slimedUrl);
        newCsrftokenCookie.setPath("/");
        httpCookieStoreSt.addCookie(newCsrftokenCookie);

        BasicClientCookie newSessionidtokenCookie = new BasicClientCookie("sessionid", sessionid);
        newSessionidtokenCookie.setVersion(1);
        newSessionidtokenCookie.setDomain(slimedUrl);
        newSessionidtokenCookie.setPath("/");
        httpCookieStoreSt.addCookie(newSessionidtokenCookie);

        client.addHeader("X-CSRFToken" , csrftoken);
        client.setCookieStore(httpCookieStoreSt);

        return client;
    };

}
