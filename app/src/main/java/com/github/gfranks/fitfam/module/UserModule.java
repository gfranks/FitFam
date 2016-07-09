package com.github.gfranks.fitfam.module;

import android.content.SharedPreferences;

import com.github.gfranks.fitfam.activity.base.BaseActivity;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.manager.FilterManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                BaseActivity.class
        },
        complete = false,
        library = true
)
public class UserModule {

    @Provides
    @Singleton
    AccountManager provideAccountManager(SharedPreferences sharedPreferences) {
        return new AccountManager(sharedPreferences);
    }

    @Provides
    @Singleton
    FilterManager provideFilterManager(SharedPreferences preferences) {
        return new FilterManager(preferences);
    }
}
