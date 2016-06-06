package com.github.gfranks.workoutcompanion.ui;

import android.app.Activity;

import butterknife.ButterKnife;

public interface AppContainer {

    /**
     * An {@link AppContainer} which returns the normal activity content view.
     */
    AppContainer DEFAULT = new AppContainer() {

        @Override
        public void setContentView(Activity activity, int layoutResId) {
            if (activity instanceof AppContainerContentInterface) {
                ((AppContainerContentInterface) activity).setActivityContent(layoutResId);
            } else {
                throw new IllegalArgumentException("Activity must inherit AppContainerContentInterface or extend BaseActivity");
            }
            ButterKnife.inject(activity);
        }
    };

    void setContentView(Activity activity, int layoutResId);
}
