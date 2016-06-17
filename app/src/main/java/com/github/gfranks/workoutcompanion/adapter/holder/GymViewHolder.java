package com.github.gfranks.workoutcompanion.adapter.holder;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.adapter.GymListAdapter;
import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.util.GymDatabase;
import com.github.gfranks.workoutcompanion.util.GymPhotoHelper;
import com.github.gfranks.workoutcompanion.util.RoundedCornersTransformation;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GymViewHolder extends WCRecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

    @Inject
    AccountManager mAccountManager;
    @Inject
    Picasso mPicasso;

    @InjectView(R.id.gym_image)
    ImageView mImage;
    @InjectView(R.id.gym_favorite)
    ToggleButton mFavorite;
    @InjectView(R.id.gym_name)
    TextView mName;
    @InjectView(R.id.gym_address)
    TextView mAddress;

    private GymListAdapter.OnFavoriteListener mListener;
    private GymDatabase mGymDatabase;

    public GymViewHolder(View view, GymListAdapter.OnFavoriteListener listener) {
        super(view);
        ButterKnife.inject(this, view);
        WorkoutCompanionApplication.get(view.getContext()).inject(this);
        mGymDatabase = new GymDatabase(view.getContext());
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

    public void populate(WCGym gym) {
        setGymImage(gym);
        mName.setText(gym.getName());
        mAddress.setText(gym.getVicinity());

        mFavorite.setOnCheckedChangeListener(null);
        try {
            mGymDatabase.open();
            mFavorite.setChecked(mGymDatabase.isFavorite(mAccountManager.getUser().getId(), gym.getId()));
            mGymDatabase.close();
        } catch (Throwable t) {
            // unable to open db
        }
        mFavorite.setOnCheckedChangeListener(this);
    }

    public void populateAsPlaceHolder(WCGym gym) {
        populate(gym);
        itemView.setClickable(false);
        mName.setEnabled(false);
        mAddress.setEnabled(false);
        mImage.setAlpha(0.5f);
        mFavorite.setEnabled(false);
    }

    private void setGymImage(WCGym gym) {
        Drawable defaultImage = new InsetDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_gym),
                (int) (100F * itemView.getResources().getDisplayMetrics().density));
        if ((gym.getPhotos() != null && !gym.getPhotos().isEmpty()) || (gym.getIcon() != null && !gym.getIcon().isEmpty())) {
            String photo = GymPhotoHelper.getRandomGymPhoto(itemView.getContext(), gym.getPhotos());
            if (photo == null || photo.isEmpty()) {
                photo = gym.getIcon();
            }
            RequestCreator creator = mPicasso.load(photo)
                    .placeholder(defaultImage)
                    .error(defaultImage);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                creator.transform(new RoundedCornersTransformation(
                        itemView.getResources().getDimensionPixelSize(R.dimen.card_view_rounded_corner_radius),
                        0, RoundedCornersTransformation.CornerType.LEFT));
            }
            creator.into(mImage);
        } else {
            mImage.setImageDrawable(defaultImage);
        }
    }

    public View getImageViewForTransition() {
        return mImage;
    }

    public View getNameViewForTransition() {
        return mName;
    }

    public View getAddressViewForTransition() {
        return mAddress;
    }
}
