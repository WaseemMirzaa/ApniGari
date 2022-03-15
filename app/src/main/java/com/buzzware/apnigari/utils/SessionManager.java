package com.buzzware.apnigari.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionManager {
    private static SharedPreferences.Editor prefsEditor;
    private static SharedPreferences prefrences;
    private static SessionManager sessionManager;

    public static SessionManager getInstance() {
        if (sessionManager == null)
            sessionManager = new SessionManager();
        return sessionManager;
    }
    public void setPermission(Context c, String message) {
        prefrences = PreferenceManager
                .getDefaultSharedPreferences(c);
        prefsEditor = prefrences.edit();
        prefsEditor.putString("Permission", message);
        prefsEditor.commit();
    }

    public String getPermission(Context c) {
        prefrences = PreferenceManager
                .getDefaultSharedPreferences(c);
        String s = prefrences.getString("Permission", null);
        return s;
    }
}
