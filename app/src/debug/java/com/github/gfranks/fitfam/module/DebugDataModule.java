package com.github.gfranks.fitfam.module;

import com.github.gfranks.fitfam.data.IsMockMode;
import com.github.gfranks.fitfam.data.api.Environment;

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
