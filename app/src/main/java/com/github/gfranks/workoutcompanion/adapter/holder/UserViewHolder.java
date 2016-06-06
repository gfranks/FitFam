package com.github.gfranks.workoutcompanion.adapter.holder;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.util.PicassoUtils;
import com.github.gfranks.workoutcompanion.util.Utils;
import com.squareup.picasso.Picasso;

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
    @InjectView(R.id.user_email)
    TextView mEmail;

    public UserViewHolder(View view) {
        super(view);
        ButterKnife.inject(this, view);
        WorkoutCompanionApplication.get(view.getContext()).inject(this);
    }

    public void populate(WCUser user) {
        mName.setText(user.getFullName());
        mEmail.setText(user.getEmail());

        Drawable defaultImage = Utils.applyDrawableTint(itemView.getContext(),
                R.drawable.ic_avatar, ContextCompat.getColor(itemView.getContext(), R.color.theme_icon_color));
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            mPicasso.load(user.getImage())
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .resize(PicassoUtils.IMAGE_THUMBNAIL_SIZE, PicassoUtils.IMAGE_THUMBNAIL_SIZE)
                    .centerCrop()
                    .into(mImage);
        } else {
            mImage.setImageDrawable(defaultImage);
        }
    }

    public View getImageViewForTransition() {
        return mImage;
    }
}