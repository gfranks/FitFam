package com.github.gfranks.workoutcompanion.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import com.github.gfranks.workoutcompanion.activity.LoginActivity;

public enum Feature {
    DEV_SETTINGS("dev_settings", false),
    CRASHLYTICS("crashlytics", true);

    public static final String FEATURE_BROADCAST = "feature_broadcast";
    public static final String FEATURE_EXTRA = "feature";

    final String mPrefsKey;
    final boolean mDefaultEnabled;

    Feature(String prefsKey, boolean defaultEnabled) {
        this.mPrefsKey = prefsKey;
        this.mDefaultEnabled = defaultEnabled;
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences("Features", Context.MODE_PRIVATE);
    }

    public boolean isEnabled(Context context) {
        return getSharedPreferences(context).getBoolean(mPrefsKey, mDefaultEnabled);
    }

    public void setEnabled(Context context, boolean enabled) {
        if (enabled == isEnabled(context)) {
            return;
        }

        setEnabled(context, enabled, false);
    }

    public void setEnabled(Context context, boolean enabled, boolean requiresRestart) {
        if (enabled == isEnabled(context)) {
            return;
        }

        // commit applied so we do this synchronously so if someone checks the feature immediately
        // after, the change will have been applied. using apply() may cause issues due to it
        // storing the value asynchronously
        getSharedPreferences(context).edit().putBoolean(mPrefsKey, enabled).commit();
        if (requiresRestart) {
            Intent newApp = new Intent(context, LoginActivity.class);
            newApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newApp);
        } else {
            Intent intent = new Intent(FEATURE_BROADCAST);
            intent.putExtra(FEATURE_EXTRA, ordinal());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }
}
