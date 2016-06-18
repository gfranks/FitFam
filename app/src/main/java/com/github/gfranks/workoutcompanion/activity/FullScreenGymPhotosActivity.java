package com.github.gfranks.workoutcompanion.activity;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.base.BaseActivity;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.util.GymUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public class FullScreenGymPhotosActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    public static final String EXTRA_INDEX = "index";

    @Inject
    Picasso mPicasso;

    @InjectView(R.id.pager)
    ViewPager mViewPager;
    @InjectView(R.id.pager_indicator)
    TextView mViewPagerIndicator;

    private WCGym mGym;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_gym_photos);

        mGym = getIntent().getParcelableExtra(WCGym.EXTRA);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(new PhotoPagerAdapter(GymUtils.getFullScreenGymPhotos(this, mGym.getPhotos())));

        mViewPager.setCurrentItem(getIntent().getIntExtra(EXTRA_INDEX, 0));
        onPageSelected(mViewPager.getCurrentItem());
        setTitle(mGym.getName());
    }

    /**
     * ******************************
     * ViewPager.OnPageChangeListener
     * ******************************
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mViewPagerIndicator.setText(++position + " of " + mGym.getPhotos().size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private class PhotoPagerAdapter extends PagerAdapter {

        private List<String> mPhotoUrls;

        PhotoPagerAdapter(List<String> photoUrls) {
            mPhotoUrls = photoUrls;
        }

        @Override
        public int getCount() {
            return mPhotoUrls.size();
        }

        public String getItem(int position) {
            return mPhotoUrls.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setFitsSystemWindows(true);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Drawable defaultImage = new InsetDrawable(ContextCompat.getDrawable(FullScreenGymPhotosActivity.this, R.drawable.ic_gym),
                    (int) (100F * getResources().getDisplayMetrics().density));
            mPicasso.load(getItem(position))
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .into(imageView);

            container.addView(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int vis = mViewPager.getSystemUiVisibility();
                    if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                        mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        mAppBarLayout.animate().translationY(0).start();
                    } else {
                        mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                        mAppBarLayout.animate().translationY((int) (-mAppBarLayout.getHeight()*1.5)).start();
                    }
                }
            });

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }
}
