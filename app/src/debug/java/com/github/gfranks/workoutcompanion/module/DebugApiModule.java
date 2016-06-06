package com.github.gfranks.workoutcompanion.module;

import android.app.Application;

import com.github.gfranks.workoutcompanion.data.IsMockMode;
import com.github.gfranks.workoutcompanion.data.api.MockWorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.util.UserDatabase;

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
        return HttpLoggingInterceptor.Level.BASIC;
    }

    @Provides
    @Singleton
    WorkoutCompanionService provideWorkoutCompanionService(Retrofit retrofit, Application application, @IsMockMode boolean isMockMode) {
        if (isMockMode) {
            return new MockWorkoutCompanionService(new UserDatabase(application));
        }
        return retrofit.create(WorkoutCompanionService.class);
    }
}