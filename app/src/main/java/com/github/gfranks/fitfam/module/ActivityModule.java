package com.github.gfranks.fitfam.module;

import android.app.Activity;
import android.content.Context;

import com.github.gfranks.fitfam.fragment.AgeSelectFragment;
import com.github.gfranks.fitfam.fragment.CreateAccountFragment;
import com.github.gfranks.fitfam.fragment.ExerciseTypeFragment;
import com.github.gfranks.fitfam.fragment.ForgotPasswordFragment;
import com.github.gfranks.fitfam.fragment.GymPhotosFragment;
import com.github.gfranks.fitfam.fragment.base.BaseDialogFragment;
import com.github.gfranks.fitfam.fragment.base.BaseFragment;
import com.github.gfranks.fitfam.ui.ForActivity;
import com.github.gfranks.fitfam.fragment.CompanionFiltersFragment;
import com.github.gfranks.fitfam.fragment.DevSettingsFragment;
import com.github.gfranks.fitfam.fragment.DiscoverFragment;
import com.github.gfranks.fitfam.fragment.DiscoverMapFragment;
import com.github.gfranks.fitfam.fragment.FavoriteGymsFragment;
import com.github.gfranks.fitfam.fragment.LoginFragment;
import com.github.gfranks.fitfam.fragment.MyCompanionsFragment;
import com.github.gfranks.fitfam.fragment.SettingsFragment;
import com.github.gfranks.fitfam.fragment.WeightSelectFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                BaseFragment.class,
                BaseDialogFragment.class,
                LoginFragment.class,
                CreateAccountFragment.class,
                ForgotPasswordFragment.class,
                DiscoverFragment.class,
                DiscoverMapFragment.class,
                FavoriteGymsFragment.class,
                MyCompanionsFragment.class,
                CompanionFiltersFragment.class,
                WeightSelectFragment.class,
                AgeSelectFragment.class,
                ExerciseTypeFragment.class,
                GymPhotosFragment.class,
                SettingsFragment.class,
                DevSettingsFragment.class
        },
        addsTo = FitFamModule.class,
        library = true,
        complete = false
)
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    /**
     * Allow the activity context to be injected but require that it be annotated with
     * {@link ForActivity @ForActivity} to explicitly differentiate it from application context.
     */
    @Provides
    @Singleton
    @ForActivity
    Context provideActivityContext() {
        return activity;
    }
}
