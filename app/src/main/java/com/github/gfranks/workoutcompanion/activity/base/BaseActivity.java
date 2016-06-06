package com.github.gfranks.workoutcompanion.activity.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;
import com.github.gfranks.workoutcompanion.module.ActivityModule;
import com.github.gfranks.workoutcompanion.ui.AppContainer;
import com.github.gfranks.workoutcompanion.ui.AppContainerContentInterface;
import com.github.gfranks.workoutcompanion.util.EndSheetBehavior;
import com.github.gfranks.workoutcompanion.util.Feature;
import com.github.gfranks.workoutcompanion.util.Utils;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import dagger.ObjectGraph;

public class BaseActivity extends AppCompatActivity implements AppContainerContentInterface, AppBarLayout.OnOffsetChangedListener,
        EndSheetBehavior.EndSheetCallback {

    @Optional
    @InjectView(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;
    @Optional
    @InjectView(R.id.coordinator_layout)
    protected CoordinatorLayout mCoordinatorLayout;
    @Optional
    @InjectView(R.id.app_bar_layout)
    protected AppBarLayout mAppBarLayout;
    @Optional
    @InjectView(R.id.toolbar)
    protected Toolbar mToolbar;
    @Optional
    @InjectView(R.id.dev_settings_container)
    protected View mDevSettings;
    protected boolean mAppBarCollapsed;
    @Inject
    AppContainer mAppContainer;
    private ObjectGraph mActivityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getInjector().inject(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.inject(this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (mDrawerLayout != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        if (mAppBarLayout != null) {
            mAppBarLayout.addOnOffsetChangedListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDevSettings != null) {
            EndSheetBehavior.from(mDevSettings).setEndSheetCallback(this);
            if (Feature.DEV_SETTINGS.isEnabled(this)) {
                mDevSettings.setVisibility(View.VISIBLE);
            } else {
                mDevSettings.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAppBarLayout != null) {
            mAppBarLayout.removeOnOffsetChangedListener(this);
        }
        ButterKnife.reset(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (popBackStackEntryOnBackPress()) {
                getSupportFragmentManager().popBackStack();
            }
        }

        Utils.hideSoftKeyboard(this);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        mAppContainer.setContentView(this, layoutResID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * ****************************
     * AppContainerContentInterface
     * ****************************
     */
    @Override
    public void setActivityContent(int layoutResID) {
        super.setContentView(layoutResID);
    }

    /**
     * ************************************
     * AppBarLayout.OnOffsetChangedListener
     * ************************************
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mAppBarCollapsed = Math.abs(verticalOffset) >= mAppBarLayout.getTotalScrollRange();
    }

    /**
     * *********************************
     * EndSheetBehavior.EndSheetCallback
     * *********************************
     */
    @Override
    public void onStateChanged(@NonNull View endSheet, @EndSheetBehavior.State int newState) {
    }

    @Override
    public void onSlide(@NonNull View endSheet, float slideOffset) {
    }

    protected boolean popBackStackEntryOnBackPress() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount <= 1) {
            supportFinishAfterTransition();
            return false;
        }
        return true;
    }

    public void inject(Object object) {
        getInjector().inject(object);
    }

    private ObjectGraph getInjector() {
        if (mActivityGraph == null) {
            mActivityGraph = WorkoutCompanionApplication.get(this).getApplicationObjectGraph().plus(getModules().toArray());
        }
        return mActivityGraph;
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new ActivityModule(this));
    }
}
