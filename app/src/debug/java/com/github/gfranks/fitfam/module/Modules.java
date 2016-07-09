package com.github.gfranks.fitfam.module;

import com.github.gfranks.fitfam.application.FitFamApplication;

public final class Modules {

    private Modules() {
        // No instances.
    }

    public static Object[] list(FitFamApplication app) {
        return new Object[]{
                new FitFamModule(app),
                new DebugLeasePulseModule(),
        };
    }
}