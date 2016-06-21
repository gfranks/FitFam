package com.github.gfranks.workoutcompanion.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.base.BaseActivity;
import com.github.gfranks.workoutcompanion.data.model.WCCompanionFilterOptions;
import com.github.gfranks.workoutcompanion.manager.FilterManager;

import javax.inject.Inject;

public class FilterCompanionsActivity extends BaseActivity {

    @Inject
    FilterManager mFilterManager;

    private WCCompanionFilterOptions mFilterOptions;
    private boolean mFiltersChanged;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_companions);

        mFilterOptions = mFilterManager.getFilterOptions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_filter_companions, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_save).setEnabled(mFiltersChanged);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFilterOptions() {

    }

    private void save() {
        mFilterManager.setFilterOptions(mFilterOptions);
        setResult(RESULT_OK); // notify the caller activity that filters have been changed
        GFMinimalNotification.make(mCoordinatorLayout, R.string.filters_saved, GFMinimalNotification.LENGTH_LONG).setCallback(new GFMinimalNotification.Callback() {
            @Override
            public void onDismissed(GFMinimalNotification notification, int event) {
                supportFinishAfterTransition();
            }
        }).show();
    }
}
