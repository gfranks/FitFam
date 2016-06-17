package com.github.gfranks.workoutcompanion.module;

import android.app.Application;

import com.github.gfranks.workoutcompanion.activity.GymDetailsActivity;
import com.github.gfranks.workoutcompanion.activity.LoginActivity;
import com.github.gfranks.workoutcompanion.activity.UserProfileActivity;
import com.github.gfranks.workoutcompanion.activity.WorkoutCompanionActivity;
import com.github.gfranks.workoutcompanion.activity.base.BaseActivity;
import com.github.gfranks.workoutcompanion.adapter.holder.GymViewHolder;
import com.github.gfranks.workoutcompanion.adapter.holder.UserViewHolder;
import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;
import com.github.gfranks.workoutcompanion.dialog.SelectGymDialog;
import com.github.gfranks.workoutcompanion.module.ui.UiModule;
import com.github.gfranks.workoutcompanion.notification.WCNotificationFactory;

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
                WorkoutCompanionActivity.class,
                UserProfileActivity.class,
                GymDetailsActivity.class,
                GymViewHolder.class,
                UserViewHolder.class,
                WCNotificationFactory.class,
                SelectGymDialog.class
        },
        library = true,
        complete = false
)
public class WorkoutCompanionModule {

    private WorkoutCompanionApplication mApplication;

    public WorkoutCompanionModule(WorkoutCompanionApplication application) {
        this.mApplication = application;
    }

    public static List<Object> list(WorkoutCompanionApplication app) {
        return Arrays.<Object>asList(
                new WorkoutCompanionModule(app)
        );
    }

    @Provides
    @Singleton
    public WorkoutCompanionApplication provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    Application provideApplicationContext() {
        return mApplication;
    }
}
