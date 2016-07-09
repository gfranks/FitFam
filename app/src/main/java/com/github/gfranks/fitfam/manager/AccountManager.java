package com.github.gfranks.fitfam.manager;

import android.content.SharedPreferences;

import com.github.gfranks.fitfam.data.model.FFUser;
import com.github.gfranks.fitfam.util.Utils;

import info.metadude.android.typedpreferences.BooleanPreference;
import info.metadude.android.typedpreferences.StringPreference;

public class AccountManager {

    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER = "user";
    private static final String KEY_GCM_REGISTERED_WITH_API = "gcm_registered_with_api";
    private static final String DEFAULT_EMAIL = "";
    private static final String DEFAULT_USER = "{}";

    private StringPreference mEmailPreference;
    private StringPreference mUserStringPreference;
    private BooleanPreference mGcmRegisteredWithApiBooleanPreference;

    private FFUser mUser;

    public AccountManager(SharedPreferences prefs) {
        mEmailPreference = new StringPreference(prefs, KEY_EMAIL, DEFAULT_EMAIL);
        mUserStringPreference = new StringPreference(prefs, KEY_USER, DEFAULT_USER);
        mGcmRegisteredWithApiBooleanPreference = new BooleanPreference(prefs, KEY_GCM_REGISTERED_WITH_API, false);
    }

    public boolean isLoggedIn() {
        return !mUserStringPreference.get().equals(DEFAULT_USER);
    }

    public String getEmail() {
        return mEmailPreference.get();
    }

    public void setEmail(String email) {
        mEmailPreference.set(email);
    }

    public FFUser getUser() {
        if (mUser == null) {
            mUser = Utils.getGson().fromJson(mUserStringPreference.get(), FFUser.class);
        }

        return mUser;
    }

    public void setUser(FFUser user) {
        mUser = user;
        mUserStringPreference.set(Utils.getGson().toJson(mUser));
        setEmail(user.getEmail());
    }

    public boolean isPushNotificationsRegisteredWithApi() {
        return mGcmRegisteredWithApiBooleanPreference.get();
    }

    public void setPushNotificationsRegisteredWithApi(boolean value) {
        mGcmRegisteredWithApiBooleanPreference.set(value);
    }

    public void login(FFUser user) {
        setUser(user);
    }

    public void logout() {
        mUserStringPreference.delete();
    }
}

