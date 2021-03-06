package com.github.gfranks.fitfam.ui;

import android.app.Activity;

import com.github.gfranks.fitfam.ui.AppContainer;
import com.github.gfranks.fitfam.util.Feature;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DebugAppContainer implements AppContainer {

    @Inject
    public DebugAppContainer() {
    }

    @Override
    public void setContentView(Activity activity, int layoutResId) {
        Feature.DEV_SETTINGS.setEnabled(activity, true);
        AppContainer.DEFAULT.setContentView(activity, layoutResId);
    }
}
