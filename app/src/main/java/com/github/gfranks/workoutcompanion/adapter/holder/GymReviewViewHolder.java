package com.github.gfranks.workoutcompanion.adapter.holder;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.data.model.WCGymReview;
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
    @InjectView(R.id.rating_bar)
    RatingBar mRatingBar;
    @InjectView(R.id.rating_text)
    TextView mText;

    public GymReviewViewHolder(View view) {
        super(view);
        ButterKnife.inject(this, view);
    }

    public void populate(WCGymReview review) {
        mUser.setText(review.getAuthor_name());
        mDate.setText(DateTimeFormat.forPattern(DATE_TIME_PATTERN).print(new DateTime(review.getTime())));
        mRatingBar.setRating(review.getRating());
        mText.setText(review.getText());

        LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.yellow),
                PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.yellow),
                PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.theme_divider),
                PorterDuff.Mode.SRC_ATOP);
    }

    public void populateAsPlaceHolder(WCGymReview review) {
        populate(review);
        mUser.setEnabled(false);
        mDate.setEnabled(false);
        mRatingBar.setEnabled(false);
        mText.setEnabled(false);
    }
}
