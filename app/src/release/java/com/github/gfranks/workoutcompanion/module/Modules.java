package com.github.gfranks.workoutcompanion.module;

import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;

public final class Modules {

    private Modules() {
        // No instances.
    }

    public static Object[] list(WorkoutCompanionApplication app) {
        return new Object[]{
                new WorkoutCompanionModule(app),
        };
    }
}