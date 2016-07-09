package com.github.gfranks.fitfam.module;

import android.app.Application;

import com.github.gfranks.fitfam.dialog.SelectGymDialog;
import com.github.gfranks.fitfam.activity.FullScreenGymPhotosActivity;
import com.github.gfranks.fitfam.activity.GymDetailsActivity;
import com.github.gfranks.fitfam.activity.GymReviewsActivity;
import com.github.gfranks.fitfam.activity.LoginActivity;
import com.github.gfranks.fitfam.activity.UserProfileActivity;
import com.github.gfranks.fitfam.activity.FitFamActivity;
import com.github.gfranks.fitfam.activity.base.BaseActivity;
import com.github.gfranks.fitfam.adapter.holder.GymViewHolder;
import com.github.gfranks.fitfam.adapter.holder.UserViewHolder;
import com.github.gfranks.fitfam.application.FitFamApplication;
import com.github.gfranks.fitfam.module.ui.UiModule;
import com.github.gfranks.fitfam.notification.FFNotificationFactory;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                UiModule.class,
                DataModule.class,
                UserModule.class,
        },
        injects = {
                BaseActivity.class,
                LoginActivity.class,
                FitFamActivity.class,
                UserProfileActivity.class,
                GymDetailsActivity.class,
                GymReviewsActivity.class,
                FullScreenGymPhotosActivity.class,
                GymViewHolder.class,
                UserViewHolder.class,
                FFNotificationFactory.class,
                SelectGymDialog.class
        },
        library = true,
        complete = false
)
public class FitFamModule {

    private FitFamApplication mApplication;

    public FitFamModule(FitFamApplication application) {
        this.mApplication = application;
    }

    public static List<Object> list(FitFamApplication app) {
        return Arrays.<Object>asList(
                new FitFamModule(app)
        );
    }

    @Provides
    @Singleton
    public FitFamApplication provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    Application provideApplicationContext() {
        return mApplication;
    }
}
