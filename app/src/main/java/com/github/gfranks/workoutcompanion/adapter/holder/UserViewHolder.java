package com.github.gfranks.workoutcompanion.adapter.holder;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.util.RoundedCornersTransformation;
import com.github.gfranks.workoutcompanion.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserViewHolder extends RecyclerView.ViewHolder {

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
        mName.setText(user.getFullName());
        if (user.getExercises().isEmpty()) {
            mExercises.setText("No exercises selected");
        } else {
            mExercises.setText(TextUtils.join(", ", user.getExercises()));
        }
        setUserImage(user);
        mName.setEnabled(false);
        mExercises.setEnabled(false);
        mImage.setAlpha(0.5f);
    }

    private void setUserImage(WCUser user) {
        Drawable defaultImage = Utils.applyDrawableTint(itemView.getContext(),
                R.drawable.ic_avatar, ContextCompat.getColor(itemView.getContext(), R.color.theme_icon_color));
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            RequestCreator creator = mPicasso.load(user.getImage())
                    .placeholder(defaultImage)
                    .error(defaultImage);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                creator.transform(new RoundedCornersTransformation(
                        itemView.getResources().getDimensionPixelSize(R.dimen.user_list_item_rounded_corner_radius),
                        0, RoundedCornersTransformation.CornerType.TOP));
            }
            creator.into(mImage);
        } else {
            mImage.setImageDrawable(defaultImage);
        }
    }

    public View getImageViewForTransition() {
        return mImage;
    }
}