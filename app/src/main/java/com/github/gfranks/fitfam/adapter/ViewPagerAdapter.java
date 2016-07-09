package com.github.gfranks.fitfam.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<TabInfo> mTabs = new ArrayList<TabInfo>();
    private Context mContext;

    public ViewPagerAdapter(Context context, FragmentManager fm, ViewPager pager) {
        super(fm);
        mContext = context;
        pager.setAdapter(this);
        pager.setOffscreenPageLimit(3);
    }

    public void addTab(Class<?> clss, Bundle args, int titleId) {
        TabInfo info = new TabInfo(clss, args, titleId);
        mTabs.add(info);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(mContext, info.clss.getName(), info.args);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int titleId = mTabs.get(position).titleId;
        if (titleId != 0 && titleId != -1) {
            return mContext.getString(titleId);
        }

        return null;
    }

    static final class TabInfo {
        private final Class<?> clss;
        private final Bundle args;
        private final int titleId;

        TabInfo(Class<?> _class, Bundle _args, int _titleId) {
            clss = _class;
            args = _args;
            titleId = _titleId;
        }
    }
}