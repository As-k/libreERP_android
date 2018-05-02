package com.cioc.libreerp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 30/04/18.
 */

public class SessionManager {
    Context context;

    SharedPreferences sp;
    SharedPreferences.Editor spe;
    private String csrfId = "csrftoken";
    private String sessionId = "sessionid";
    private String STATUS = "status";

    public SessionManager(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("registered_status", context.MODE_PRIVATE);
        spe = sp.edit();
    }

    public String getCsrfId() {
        return sp.getString(csrfId, "");
    }

    public void setCsrfId(String csrf) {
        spe.putString(csrfId, csrf);
        spe.commit();
    }

    public String getSessionId() {
        return sp.getString(sessionId, "");
    }

    public void setSessionId(String session) {
        spe.putString(sessionId, session);
        spe.commit();
    }

    public boolean getStatus() {
        return sp.getBoolean(STATUS, false);
    }

    public void setSTATUS(boolean status) {
        spe.putBoolean(STATUS, status);
        spe.commit();
    }

    public void clearAll(){
        spe.clear();
        spe.apply();
    }
}
