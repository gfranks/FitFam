package com.github.gfranks.workoutcompanion.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.data.model.WCGymReview;
import com.github.gfranks.workoutcompanion.util.GymUtils;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GymReviewViewHolder extends WCRecyclerView.ViewHolder {

    public static final String DATE_TIME_PATTERN = "MMMM dd, yyyy";

    @InjectView(R.id.rating_user)
    TextView mUser;
    @InjectView(R.id.rating_date)
    TextView mDate;
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
    @InjectView(R.id.rating_text)
    TextView mText;

    public GymReviewViewHolder(View view) {
        super(view);
        ButterKnife.inject(this, view);
    }

    public void populate(WCGymReview review) {
        mUser.setText(review.getAuthor_name());
        mDate.setText(DateTimeFormat.forPattern(DATE_TIME_PATTERN).print(new DateTime(review.getTime())));
        GymUtils.adjustImageViewsForRating(itemView.getContext(), review.getRating(), new ImageView[]{
                mRating1, mRating2, mRating3, mRating4, mRating5
        });
        mText.setText(review.getText());
    }

    public void populateAsPlaceHolder(WCGymReview review) {
        populate(review);
        mUser.setEnabled(false);
        mDate.setEnabled(false);
        mRating1.setEnabled(false);
        mRating2.setEnabled(false);
        mRating3.setEnabled(false);
        mRating4.setEnabled(false);
        mRating5.setEnabled(false);
        mText.setEnabled(false);
    }
}
