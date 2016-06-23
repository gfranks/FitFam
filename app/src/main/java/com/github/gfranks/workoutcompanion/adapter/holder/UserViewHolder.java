package com.github.gfranks.workoutcompanion.adapter.holder;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.util.RoundedCornersTransformation;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserViewHolder extends WCRecyclerView.ViewHolder {

    @Inject
    Picasso mPicasso;

    @InjectView(R.id.user_image)
    ImageView mImage;
    @InjectView(R.id.user_name)
    TextView mName;
    @InjectView(R.id.user_exercises)
    TextView mExercises;

    public UserViewHolder(View view) {
        super(view);
        ButterKnife.inject(this, view);
        WorkoutCompanionApplication.get(view.getContext()).inject(this);
    }

    public void populate(WCUser user) {
        mName.setText(user.getFullName());
        if (user.getExercises().isEmpty()) {
            mExercises.setText("No exercises selected");
        } else {
            mExercises.setText(TextUtils.join(", ", user.getExercises()));
        }

        setUserImage(user);
    }

    public void populateAsPlaceHolder(WCUser user) {
        populate(user);
        itemView.setClickable(false);
        mName.setEnabled(false);
        mExercises.setEnabled(false);
        mImage.setAlpha(0.5f);
    }

    private void setUserImage(WCUser user) {
        Drawable defaultImage = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_avatar);
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            RequestCreator creator = mPicasso.load(user.getImage())
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
}