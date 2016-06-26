package com.github.gfranks.workoutcompanion.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.base.BaseActivity;
import com.github.gfranks.workoutcompanion.adapter.GymReviewListAdapter;
import com.github.gfranks.workoutcompanion.adapter.holder.GymReviewViewHolder;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.data.model.WCGymReview;
import com.github.gfranks.workoutcompanion.view.WCEmptyView;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;

import butterknife.InjectView;

public class GymReviewsActivity extends BaseActivity implements WCRecyclerView.OnItemClickListener {

    @InjectView(R.id.list)
    WCRecyclerView mListView;
    @InjectView(R.id.list_empty_text)
    WCEmptyView mEmptyView;

    private WCGym mGym;
    private GymReviewListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_reviews);

        mGym = getIntent().getParcelableExtra(WCGym.EXTRA);

        setupEmptyView();
        mListView.setOnItemClickListener(this);
        mAdapter = new GymReviewListAdapter(mGym.getReviews());
        mListView.setAdapter(mAdapter);

        setTitle(mGym.getReviews().size() + " " + getString(R.string.gym_reviews));
    }

    /**
     * **********************************
     * WCRecyclerView.OnItemClickListener
     * **********************************
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, int position) {
        WCGymReview review = mAdapter.getItem(position);
        if (review.getAuthor_url() != null && review.getAuthor_url().length() > 0) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(review.getAuthor_url()));
            startActivity(intent);
        }
    }

    private void setupEmptyView() {
        mEmptyView.setTitle(R.string.empty_reviews_title);
        mEmptyView.setSubtitle(R.string.empty_reviews_subtitle);
        View emptyHeader = mEmptyView.addEmptyHeader(R.layout.layout_gym_review_list_item);
        emptyHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.theme_background_dark));
        new GymReviewViewHolder(emptyHeader).populateAsPlaceHolder(new WCGymReview.Builder()
                .setAuthor_name(getString(R.string.empty_companions_header_first_name) + " " + getString(R.string.empty_companions_header_last_name))
                .setRating(4.5f)
                .setText("Great equipment setup and staff! This is definitely my home gym!")
                .build());
        mListView.setEmptyView(mEmptyView);
    }
}
