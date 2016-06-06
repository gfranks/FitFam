package com.github.gfranks.workoutcompanion.module;

import com.github.gfranks.workoutcompanion.module.ui.DebugUiModule;

import dagger.Module;

@Module(
        addsTo = WorkoutCompanionModule.class,
        includes = {
                DebugUiModule.class,
                DebugDataModule.class
        },
        overrides = true,
        complete = false
)
public final class DebugLeasePulseModule {
}