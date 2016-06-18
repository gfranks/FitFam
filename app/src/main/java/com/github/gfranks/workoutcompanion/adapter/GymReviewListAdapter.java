package com.github.gfranks.workoutcompanion.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.adapter.holder.GymReviewViewHolder;
import com.github.gfranks.workoutcompanion.data.model.WCGymReview;

import java.util.List;

public class GymReviewListAdapter extends RecyclerView.Adapter<GymReviewViewHolder> {

    List<WCGymReview> mReviews;

    public GymReviewListAdapter() {
    }

    public GymReviewListAdapter(List<WCGymReview> reviews) {
        mReviews = reviews;
    }

    public void setReviews(List<WCGymReview> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public WCGymReview getItem(int position) {
        return mReviews.get(position);
    }

    @Override
    public GymReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GymReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gym_review_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(GymReviewViewHolder holder, int position) {
        holder.populate(getItem(position));
    }
}
