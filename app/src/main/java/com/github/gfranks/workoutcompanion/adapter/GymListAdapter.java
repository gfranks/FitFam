package com.github.gfranks.workoutcompanion.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.adapter.holder.GymViewHolder;
import com.github.gfranks.workoutcompanion.data.model.WCGym;

import java.util.ArrayList;
import java.util.List;

public class GymListAdapter extends RecyclerView.Adapter<GymViewHolder> {

    private List<WCGym> mGyms;

    public GymListAdapter() {
        mGyms = new ArrayList<>();
    }

    public GymListAdapter(List<WCGym> gyms) {
        mGyms = gyms;
    }

    public void setGyms(List<WCGym> gyms) {
        mGyms = gyms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mGyms.size();
    }

    public WCGym getItem(int position) {
        return mGyms.get(position);
    }

    @Override
    public GymViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GymViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gym_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(GymViewHolder holder, int position) {
        holder.populate(getItem(position));
    }
}
