package com.github.gfranks.fitfam.util;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;
import android.transition.Transition;

public class AnimationUtils {

    public static final int DEFAULT_FAB_ANIM_DURATION = 175;

    public static class DefaultAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static class DefaultTransitionListener implements Transition.TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }
}
