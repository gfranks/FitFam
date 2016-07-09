package com.github.gfranks.fitfam.fragment.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.activity.base.BaseActivity;
import com.github.gfranks.fitfam.util.Utils;

import butterknife.ButterKnife;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).inject(this);

        setupTransition();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Utils.applyMenuTintColor(getActivity(), menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utils.hideSoftKeyboard(getActivity());
        return super.onOptionsItemSelected(item);
    }

    protected void setupTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slideInTransition;
            try {
                slideInTransition = new Slide(Gravity.END);
            } catch (Throwable t) {
                slideInTransition = new Slide(Gravity.RIGHT);
            }
            slideInTransition.setDuration(200);

            setEnterTransition(slideInTransition);
            setAllowEnterTransitionOverlap(false);
            setAllowReturnTransitionOverlap(false);
            setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.transition_default));

            Slide slideOutTransition;
            try {
                slideOutTransition = new Slide(Gravity.START);
            } catch (Throwable t) {
                slideOutTransition = new Slide(Gravity.LEFT);
            }
            slideOutTransition.setDuration(200);

            setReenterTransition(slideOutTransition);
            setExitTransition(slideOutTransition);
        }
    }
}
