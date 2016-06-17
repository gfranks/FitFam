package com.github.gfranks.workoutcompanion.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.FullScreenGymPhotosActivity;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.util.GymPhotoHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public class GymPhotosFragment extends BaseFragment {

    public static final String TAG = "gym_photos_fragment";

    @Inject
    Picasso mPicasso;

    @InjectView(R.id.pager)
    ViewPager mViewPager;

    private WCGym mGym;
    private PhotoPagerAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGym = getArguments().getParcelable(WCGym.EXTRA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gym_photos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager.setOffscreenPageLimit(3);
        setGym(mGym);
    }

    public void setGym(WCGym gym) {
        mGym = gym;

        if (isDetached() || getActivity() == null || mGym == null || mGym.getPhotos() == null) {
            return;
        }

        mAdapter = new PhotoPagerAdapter(GymPhotoHelper.getScaledGymPhotos(getContext(), mGym.getPhotos()));
        mViewPager.setAdapter(mAdapter);
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

            Drawable defaultImage = new InsetDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_gym),
                    (int) (75F * getResources().getDisplayMetrics().density));
            mPicasso.load(getItem(position))
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .into(imageView);

            container.addView(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FullScreenGymPhotosActivity.class);
                    intent.putExtra(WCGym.EXTRA, mGym);
                    intent.putExtra(FullScreenGymPhotosActivity.EXTRA_INDEX, mViewPager.getCurrentItem());
                    startActivity(intent);
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
