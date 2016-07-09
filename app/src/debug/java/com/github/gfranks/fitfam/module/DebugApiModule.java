package com.github.gfranks.fitfam.module;

import android.app.Application;
import android.content.SharedPreferences;

import com.github.gfranks.fitfam.data.IsMockMode;
import com.github.gfranks.fitfam.data.api.MockFitFamService;
import com.github.gfranks.fitfam.data.api.FitFamService;
import com.github.gfranks.fitfam.manager.AccountManager;

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
    MockFitFamService provideMockFitFamService(Application app, AccountManager accountManager, SharedPreferences prefs) {
        return new MockFitFamService(app, accountManager, prefs);
    }

    @Provides
    @Singleton
    FitFamService provideFitFamService(Retrofit retrofit, MockFitFamService mockFitFamService,
                                                 @IsMockMode boolean isMockMode) {
        if (isMockMode) {
            return mockFitFamService;
        }
        return retrofit.create(FitFamService.class);
    }
}