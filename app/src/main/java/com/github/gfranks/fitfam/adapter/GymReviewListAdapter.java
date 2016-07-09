package com.github.gfranks.fitfam.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.gfranks.fitfam.adapter.holder.GymReviewViewHolder;
import com.github.gfranks.fitfam.data.model.FFGymReview;
import com.github.gfranks.fitfam.R;

import java.util.List;

public class GymReviewListAdapter extends RecyclerView.Adapter<GymReviewViewHolder> {

    List<FFGymReview> mReviews;

    public GymReviewListAdapter() {
    }

    public GymReviewListAdapter(List<FFGymReview> reviews) {
        mReviews = reviews;
    }

    public void setReviews(List<FFGymReview> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public FFGymReview getItem(int position) {
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
