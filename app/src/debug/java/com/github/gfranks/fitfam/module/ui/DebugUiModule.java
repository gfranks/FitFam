package com.github.gfranks.fitfam.module.ui;

import com.github.gfranks.fitfam.ui.AppContainer;
import com.github.gfranks.fitfam.ui.DebugAppContainer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = DebugAppContainer.class,
        complete = false,
        library = true,
        overrides = true
)
public class DebugUiModule {
    @Provides
    @Singleton
    AppContainer provideAppContainer(DebugAppContainer debugAppContainer) {
        //return AppContainer.DEFAULT;
        return debugAppContainer;
    }
}