package com.github.gfranks.fitfam.module.ui;

import com.github.gfranks.fitfam.ui.AppContainer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false
)

public class UiModule {
    @Provides
    @Singleton
    AppContainer provideAppContainer() {
        return AppContainer.DEFAULT;
    }
}
