package com.github.gfranks.workoutcompanion.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.gfranks.workoutcompanion.BuildConfig;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                ApiModule.class
        },
        injects = {
        },
        complete = false,
        library = true
)
public class DataModule {

    static final String CACHE = "network_cache";

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    public File provideCacheDir(Application app) {
        File cache = new File(app.getCacheDir(), CACHE);
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    @Provides
    @Singleton
    Picasso providePicasso(Application app) {
        Picasso picasso = Picasso.with(app);
        picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
        picasso.setLoggingEnabled(BuildConfig.DEBUG);
        return picasso;
    }
}
