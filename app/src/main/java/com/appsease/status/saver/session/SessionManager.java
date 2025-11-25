package com.appsease.status.saver.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String TAG = "SessionManager";
    private Context context;
    private static SessionManager sessionManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int PRIVATE_MODE = 0;
    public static final String KEY_APP_OPEN_COUNT = "app_open_count";
    public static final String KEY_IS_RATE_GIVEN = "is_rate_given";

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }
        return sessionManager;
    }

    public void initSessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getApplicationInfo().packageName, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void setAppOpenCount(int count) {
        editor.putInt(KEY_APP_OPEN_COUNT, count);
        editor.commit();
    }

    public int getAppOpenCount() {
        return sharedPreferences.getInt(KEY_APP_OPEN_COUNT, 0);
    }


    public void setRateGiven(boolean isRateGiven) {
        editor.putBoolean(KEY_IS_RATE_GIVEN, isRateGiven);
        editor.commit();
    }

    public boolean isRateGiven() {
        return sharedPreferences.getBoolean(KEY_IS_RATE_GIVEN, false);
    }
}
