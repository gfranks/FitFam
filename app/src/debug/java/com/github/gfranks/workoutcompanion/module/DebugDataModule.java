package com.github.gfranks.workoutcompanion.module;

import com.github.gfranks.workoutcompanion.data.IsMockMode;
import com.github.gfranks.workoutcompanion.data.api.Environment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                DebugApiModule.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public final class DebugDataModule {

    @Provides
    @Singleton
    @IsMockMode
    boolean provideIsMockMode(Environment environment) {
        return environment.isMockMode();
    }
}
