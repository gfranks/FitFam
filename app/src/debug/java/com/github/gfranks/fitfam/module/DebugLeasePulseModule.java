package com.github.gfranks.fitfam.module;

import com.github.gfranks.fitfam.module.ui.DebugUiModule;

import dagger.Module;

@Module(
        addsTo = FitFamModule.class,
        includes = {
                DebugUiModule.class,
                DebugDataModule.class
        },
        overrides = true,
        complete = false
)
public final class DebugLeasePulseModule {
}