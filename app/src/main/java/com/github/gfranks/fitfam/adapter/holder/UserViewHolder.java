package com.github.gfranks.fitfam.adapter.holder;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.gfranks.fitfam.application.FitFamApplication;
import com.github.gfranks.fitfam.data.model.FFUser;
import com.github.gfranks.fitfam.view.FFRecyclerView;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.util.RoundedCornersTransformation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserViewHolder extends FFRecyclerView.ViewHolder {

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
        FitFamApplication.get(view.getContext()).inject(this);
    }

    public void populate(FFUser user) {
        mName.setText(user.getFullName());
        if (user.getExercises().isEmpty()) {
            mExercises.setText("No exercises selected");
        } else {
            mExercises.setText(TextUtils.join(", ", user.getExercises()));
        }

        setUserImage(user);
    }

    public void populateAsPlaceHolder(FFUser user) {
        populate(user);
        itemView.setClickable(false);
        mName.setEnabled(false);
        mExercises.setEnabled(false);
        mImage.setAlpha(0.5f);
    }

    private void setUserImage(FFUser user) {
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