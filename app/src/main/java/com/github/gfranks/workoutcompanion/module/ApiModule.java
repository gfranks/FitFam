package com.github.gfranks.workoutcompanion.module;

import android.app.Application;
import android.content.SharedPreferences;

import com.github.gfranks.workoutcompanion.BuildConfig;
import com.github.gfranks.workoutcompanion.data.api.GoogleApiService;
import com.github.gfranks.workoutcompanion.data.api.Endpoint;
import com.github.gfranks.workoutcompanion.data.api.Environment;
import com.github.gfranks.workoutcompanion.data.api.ErrorHandlingExecutorCallAdapterFactory;
import com.github.gfranks.workoutcompanion.data.api.RequestHeaderInterceptor;
import com.github.gfranks.workoutcompanion.data.api.RequestLoggingInterceptor;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.manager.GoogleApiManager;
import com.github.gfranks.workoutcompanion.util.Utils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(
        injects = {
        },
        library = true,
        complete = false
)
public class ApiModule {

    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    @Provides
    @Singleton
    HttpLoggingInterceptor.Level provideHttpLoggingInterceptorLevel() {
        return HttpLoggingInterceptor.Level.NONE;
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor(HttpLoggingInterceptor.Level logLevel) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(logLevel);
        return interceptor;
    }

    @Provides
    @Singleton
    RequestLoggingInterceptor provideRequestLoggingInterceptor(HttpLoggingInterceptor interceptor) {
        return new RequestLoggingInterceptor(interceptor);
    }

    @Provides
    @Singleton
    ErrorHandlingExecutorCallAdapterFactory provideErrorHandlingExecutorCallAdapterFactory(Application app, AccountManager accountManager) {
        return new ErrorHandlingExecutorCallAdapterFactory(app, accountManager);
    }

    @Provides
    @Singleton
    OkHttpClient provideClient(RequestHeaderInterceptor interceptor, RequestLoggingInterceptor loggingInterceptor, HttpLoggingInterceptor httpLoggingInterceptor, File cacheDir) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(interceptor)
                .cache(new Cache(cacheDir, DISK_CACHE_SIZE))
                .connectTimeout(7000, TimeUnit.MILLISECONDS)
                .readTimeout(13000, TimeUnit.MILLISECONDS)
                .writeTimeout(3000, TimeUnit.MILLISECONDS)
                .build();
    }

    @Provides
    @Singleton
    Environment provideEnvironment(SharedPreferences preferences) {
        return Environment.values()[preferences.getInt(Environment.ENVIRONMENT, Environment.getDefault().ordinal())];
    }

    @Provides
    @Singleton
    Endpoint provideEndpoint(Environment environment) {
        return Endpoint.newFixedEndpoint(environment.url());
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Endpoint endpoint, OkHttpClient client, ErrorHandlingExecutorCallAdapterFactory errorHandlingFactory) {
        return new Retrofit.Builder()
                .baseUrl(endpoint.url())
                .addConverterFactory(GsonConverterFactory.create(Utils.getGson()))
                .addCallAdapterFactory(errorHandlingFactory)
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    WorkoutCompanionService provideWorkoutCompanionService(Retrofit retrofit) {
        return retrofit.create(WorkoutCompanionService.class);
    }

    @Provides
    @Singleton
    GoogleApiService provideGoogleApiService(OkHttpClient client, ErrorHandlingExecutorCallAdapterFactory errorHandlingFactory) {
        return provideRetrofit(Endpoint.newFixedEndpoint(BuildConfig.API_GOOGLE_PLACES),
                client, errorHandlingFactory).create(GoogleApiService.class);
    }

    @Provides
    @Singleton
    GoogleApiManager provideGoogleApiManager(SharedPreferences prefs, Application app) {
        return new GoogleApiManager(prefs, app);
    }

}
