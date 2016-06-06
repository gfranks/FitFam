package com.github.gfranks.workoutcompanion.data.api;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.util.Log;

import com.github.gfranks.workoutcompanion.BuildConfig;
import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public final class RequestHeaderInterceptor implements Interceptor {

    private static final int MAX_AGE = 60;
    private static final int MAX_STALE = 60 * 60 * 24 * 28;
    private static final String CACHE_MAX_AGE = "public, max-age=" + MAX_AGE;
    private static final String CACHE_MAX_STALE = "public, only-if-cached, max-stale=" + MAX_STALE;

    private static final String API_VERSION = "Api-Version";
    private static final String API_VERSION_VALUE = "1";
    private static final String APP_VERSION = "App-Version";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String DEVICE_TYPE = "Device-Type";
    private static final String DEVICE_TYPE_VALUE = "android";
    private static String ANDROID_VERSION;

    private final ConnectivityManager mConnectivityManager;

    @Inject
    public RequestHeaderInterceptor(Application app) {
        ANDROID_VERSION = "";
        try {
            ANDROID_VERSION = String.valueOf(app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mConnectivityManager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (BuildConfig.DEBUG) {
            Log.d(WorkoutCompanionApplication.TAG, "Intercept Request for Headers");
        }

        Request.Builder newBuilder = request.newBuilder()
                .addHeader(API_VERSION, API_VERSION_VALUE)
                .addHeader(APP_VERSION, ANDROID_VERSION)
                .addHeader(DEVICE_TYPE, DEVICE_TYPE_VALUE);

        if (mConnectivityManager != null && mConnectivityManager.getActiveNetworkInfo() != null) {
            String cacheControlValue;
            if (mConnectivityManager.getActiveNetworkInfo().isConnected()) {
                cacheControlValue = CACHE_MAX_AGE;
            } else {
                cacheControlValue = CACHE_MAX_STALE;
            }
            newBuilder.addHeader(CACHE_CONTROL, cacheControlValue);
        }

        return chain.proceed(newBuilder.build());
    }
}
