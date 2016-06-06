package com.github.gfranks.workoutcompanion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.adapter.ViewPagerAdapter;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;

import butterknife.InjectView;

public class DiscoverFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {

    public static final String TAG = "discover_fragment";

    @InjectView(R.id.tabs)
    TabLayout mTabLayout;
    @InjectView(R.id.pager)
    ViewPager mViewPager;

    private ViewPagerAdapter mAdapter;

    public static DiscoverFragment newInstance() {
        return new DiscoverFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new ViewPagerAdapter(getContext(), getChildFragmentManager(), mViewPager);
        mAdapter.addTab(DiscoverMapFragment.class, getArguments(), R.string.discover_gyms);
        mAdapter.addTab(MyCompanionsFragment.class, getArguments(), R.string.discover_workout_companions);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(this);

        onTabSelected(mTabLayout.getTabAt(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.nav_discover);
    }

    /**
     * *******************************
     * TabLayout.OnTabSelectedListener
     * *******************************
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}
