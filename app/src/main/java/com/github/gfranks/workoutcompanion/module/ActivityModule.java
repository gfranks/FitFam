package com.github.gfranks.workoutcompanion.module;

import android.app.Activity;
import android.content.Context;

import com.github.gfranks.workoutcompanion.fragment.CreateAccountFragment;
import com.github.gfranks.workoutcompanion.fragment.DevSettingsFragment;
import com.github.gfranks.workoutcompanion.fragment.DiscoverFragment;
import com.github.gfranks.workoutcompanion.fragment.DiscoverMapFragment;
import com.github.gfranks.workoutcompanion.fragment.ExerciseTypeFragment;
import com.github.gfranks.workoutcompanion.fragment.FavoriteGymsFragment;
import com.github.gfranks.workoutcompanion.fragment.ForgotPasswordFragment;
import com.github.gfranks.workoutcompanion.fragment.GymPhotosFragment;
import com.github.gfranks.workoutcompanion.fragment.LoginFragment;
import com.github.gfranks.workoutcompanion.fragment.MyCompanionsFragment;
import com.github.gfranks.workoutcompanion.fragment.SettingsFragment;
import com.github.gfranks.workoutcompanion.fragment.WeightSelectFragment;
import com.github.gfranks.workoutcompanion.fragment.base.BaseDialogFragment;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.ui.ForActivity;

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
                WeightSelectFragment.class,
                ExerciseTypeFragment.class,
                GymPhotosFragment.class,
                SettingsFragment.class,
                DevSettingsFragment.class
        },
        addsTo = WorkoutCompanionModule.class,
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
     * {@link com.github.gfranks.workoutcompanion.ui.ForActivity @ForActivity} to explicitly differentiate it from application context.
     */
    @Provides
    @Singleton
    @ForActivity
    Context provideActivityContext() {
        return activity;
    }
}
