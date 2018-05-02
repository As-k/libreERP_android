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
        sp = context.getSharedPreferences("registered_status", Context.MODE_PRIVATE);

    }

    public String getCsrfId() {
        return sp.getString(csrfId, "");
    }

    public void setCsrfId(String csrf) {
        spe = sp.edit();
        spe.putString(csrfId, csrf);
        spe.apply();
    }

    public String getSessionId() {
        return sp.getString(sessionId, "");
    }

    public void setSessionId(String session) {
        spe = sp.edit();
        spe.putString(sessionId, session);
        spe.apply();
    }

    public boolean getStatus() {
        return sp.getBoolean(STATUS, false);
    }

    public void setSTATUS(boolean status) {
        spe.putBoolean(STATUS, status);
        spe.commit();
    }

    public void clearAll(){
        spe = sp.edit();
        spe.clear();
        spe.apply();
    }
}
