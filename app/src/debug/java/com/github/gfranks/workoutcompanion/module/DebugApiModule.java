package com.github.gfranks.workoutcompanion.module;

import android.app.Application;
import android.content.SharedPreferences;

import com.github.gfranks.workoutcompanion.data.IsMockMode;
import com.github.gfranks.workoutcompanion.data.api.MockWorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.manager.AccountManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

@Module(
        library = true,
        overrides = true,
        complete = false
)
public final class DebugApiModule {

    @Provides
    @Singleton
    HttpLoggingInterceptor.Level provideHttpLoggingInterceptorLevel() {
        return HttpLoggingInterceptor.Level.BODY;
    }

    @Provides
    @Singleton
    MockWorkoutCompanionService provideMockWorkoutCompanionService(Application app, AccountManager accountManager, SharedPreferences prefs) {
        return new MockWorkoutCompanionService(app, accountManager, prefs);
    }

    @Provides
    @Singleton
    WorkoutCompanionService provideWorkoutCompanionService(Retrofit retrofit, MockWorkoutCompanionService mockWorkoutCompanionService,
                                                           @IsMockMode boolean isMockMode) {
        if (isMockMode) {
            return mockWorkoutCompanionService;
        }
        return retrofit.create(WorkoutCompanionService.class);
    }
}