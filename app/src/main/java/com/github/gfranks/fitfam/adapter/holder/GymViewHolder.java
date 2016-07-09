package com.github.gfranks.fitfam.adapter.holder;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.gfranks.fitfam.adapter.GymListAdapter;
import com.github.gfranks.fitfam.application.FitFamApplication;
import com.github.gfranks.fitfam.data.model.FFGym;
import com.github.gfranks.fitfam.util.GymDatabase;
import com.github.gfranks.fitfam.util.GymUtils;
import com.github.gfranks.fitfam.view.FFRecyclerView;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.util.RoundedCornersTransformation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GymViewHolder extends FFRecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

    @Inject
    AccountManager mAccountManager;
    @Inject
    Picasso mPicasso;

    @InjectView(R.id.gym_image)
    ImageView mImage;
    @InjectView(R.id.gym_favorite)
    ToggleButton mFavorite;
    @InjectView(R.id.rating_view)
    View mRating;
    @InjectView(R.id.rating_1_image_view)
    ImageView mRating1;
    @InjectView(R.id.rating_2_image_view)
    ImageView mRating2;
    @InjectView(R.id.rating_3_image_view)
    ImageView mRating3;
    @InjectView(R.id.rating_4_image_view)
    ImageView mRating4;
    @InjectView(R.id.rating_5_image_view)
    ImageView mRating5;
    @InjectView(R.id.gym_name)
    TextView mName;
    @InjectView(R.id.gym_address)
    TextView mAddress;

    private GymListAdapter.OnFavoriteListener mListener;

    public GymViewHolder(View view, GymListAdapter.OnFavoriteListener listener) {
        super(view);
        ButterKnife.inject(this, view);
        FitFamApplication.get(view.getContext()).inject(this);
        mListener = listener;
    }

    /**
     * **************************************
     * CompoundButton.OnCheckedChangeListener
     * **************************************
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mListener != null) {
            mListener.onFavorite(getAdapterPosition(), isChecked);
        }
    }

    public void populate(GymDatabase gymDatabase, FFGym gym) {
        setGymImage(gym);
        mName.setText(gym.getName());
        mAddress.setText(gym.getVicinity());

        if (gym.getRating() > 0) {
            mRating.setVisibility(View.VISIBLE);
            GymUtils.adjustImageViewsForRating(itemView.getContext(), gym.getRating(), new ImageView[]{
                    mRating1, mRating2, mRating3, mRating4, mRating5
            });
        } else {
            mRating.setVisibility(View.GONE);
        }

        mFavorite.setOnCheckedChangeListener(null);
        try {
            gymDatabase.open();
            mFavorite.setChecked(gymDatabase.isFavorite(mAccountManager.getUser().getId(), gym.getId()));
            gymDatabase.close();
        } catch (Throwable t) {
            // unable to open db
        }
        mFavorite.setOnCheckedChangeListener(this);
    }

    public void populateAsPlaceHolder(FFGym gym) {
        populate(null, gym);
        itemView.setClickable(false);
        mName.setEnabled(false);
        mAddress.setEnabled(false);
        mImage.setAlpha(0.5f);
        mFavorite.setEnabled(false);
        mRating1.setEnabled(false);
        mRating1.setAlpha(0.5f);
        mRating2.setEnabled(false);
        mRating2.setAlpha(0.5f);
        mRating3.setEnabled(false);
        mRating3.setAlpha(0.5f);
        mRating4.setEnabled(false);
        mRating4.setAlpha(0.5f);
        mRating5.setEnabled(false);
        mRating5.setAlpha(0.5f);
    }

    private void setGymImage(FFGym gym) {
        Drawable defaultImage = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_gym);
        if ((gym.getPhotos() != null && !gym.getPhotos().isEmpty()) || (gym.getIcon() != null && !gym.getIcon().isEmpty())) {
            mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String photo = GymUtils.getRandomGymPhoto(itemView.getContext(), gym.getPhotos());
            if (photo == null || photo.isEmpty()) {
                photo = gym.getIcon();
            }
            RequestCreator creator = mPicasso.load(photo)
                    .placeholder(defaultImage)
                    .error(defaultImage);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                creator.transform(new RoundedCornersTransformation(
                        itemView.getResources().getDimensionPixelSize(R.dimen.card_view_rounded_corner_radius),
                        0, RoundedCornersTransformation.CornerType.TOP));
            }
            creator.into(mImage, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    mImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            });
        } else {
            mImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mImage.setImageDrawable(defaultImage);
        }
    }

    public View getImageViewForTransition() {
        return mImage;
    }

    public View getFavoriteViewForTransition() {
        return mFavorite;
    }

    public View getNameViewForTransition() {
        return mName;
    }

    public View getAddressViewForTransition() {
        return mAddress;
    }
}
