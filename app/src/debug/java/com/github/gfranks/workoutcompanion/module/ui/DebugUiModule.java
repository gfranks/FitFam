package com.github.gfranks.workoutcompanion.module.ui;

import com.github.gfranks.workoutcompanion.ui.AppContainer;
import com.github.gfranks.workoutcompanion.ui.DebugAppContainer;

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