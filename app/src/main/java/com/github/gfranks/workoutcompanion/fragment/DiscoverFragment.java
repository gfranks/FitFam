package com.github.gfranks.workoutcompanion.fragment;

import android.content.SharedPreferences;
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
import com.github.gfranks.workoutcompanion.util.Utils;

import javax.inject.Inject;

import butterknife.InjectView;

public class DiscoverFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {

    public static final String TAG = "discover_fragment";
    private static final String PAGE = "page";

    @Inject
    SharedPreferences mPrefs;

    @InjectView(R.id.tabs)
    TabLayout mTabLayout;
    @InjectView(R.id.pager)
    ViewPager mViewPager;

    private ViewPagerAdapter mAdapter;

    public static DiscoverFragment newInstance() {
        return new DiscoverFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        mAdapter.addTab(DiscoverMapFragment.class, getArguments(), 0);
        mAdapter.addTab(FavoriteGymsFragment.class, getArguments(), 0);
        mAdapter.addTab(MyCompanionsFragment.class, getArguments(), 0);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(this);

        mTabLayout.getTabAt(0).setIcon(Utils.getTabIcon(getActivity(), R.drawable.ic_discover));
        mTabLayout.getTabAt(1).setIcon(Utils.getTabIcon(getActivity(), R.drawable.ic_gym));
        mTabLayout.getTabAt(2).setIcon(Utils.getTabIcon(getActivity(), R.drawable.ic_users));

        onTabSelected(mTabLayout.getTabAt(mPrefs.getInt(PAGE, 0)));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE, mViewPager.getCurrentItem());
    }

    /**
     * *******************************
     * TabLayout.OnTabSelectedListener
     * *******************************
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()) {
            case 0:
                getActivity().setTitle(R.string.discover_gyms);
                break;
            case 1:
                getActivity().setTitle(R.string.discover_saved_gyms);
                break;
            case 2:
                getActivity().setTitle(R.string.discover_my_companions);
                break;
        }
        mPrefs.edit().putInt(PAGE, tab.getPosition()).apply();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}
