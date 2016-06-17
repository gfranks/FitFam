package com.github.gfranks.workoutcompanion.adapter.holder;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.util.RoundedCornersTransformation;
import com.github.gfranks.workoutcompanion.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GymViewHolder extends RecyclerView.ViewHolder {

    @Inject
    Picasso mPicasso;

    @InjectView(R.id.gym_image)
    ImageView mImage;
    @InjectView(R.id.gym_name)
    TextView mName;
    @InjectView(R.id.gym_address)
    TextView mAddress;

    public GymViewHolder(View view) {
        super(view);
        ButterKnife.inject(this, view);
        WorkoutCompanionApplication.get(view.getContext()).inject(this);
    }

    public void populate(WCGym gym) {
        setGymImage(gym);
        mName.setText(gym.getName());
        mAddress.setText(gym.getVicinity());
    }

    public void populateAsPlaceHolder(WCGym gym) {
        populate(gym);
        mName.setEnabled(false);
        mAddress.setEnabled(false);
        mImage.setAlpha(0.5f);
    }

    private void setGymImage(WCGym gym) {
        Drawable defaultImage = Utils.applyDrawableTint(itemView.getContext(),
                R.drawable.ic_gym, ContextCompat.getColor(itemView.getContext(), R.color.theme_icon_color));
        if (gym.getIcon() != null && !gym.getIcon().isEmpty()) {
            RequestCreator creator = mPicasso.load(gym.getIcon())
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
}
