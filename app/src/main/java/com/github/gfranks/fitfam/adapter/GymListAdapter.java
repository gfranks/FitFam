package com.github.gfranks.fitfam.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.gfranks.fitfam.data.model.FFGym;
import com.github.gfranks.fitfam.util.GymDatabase;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.adapter.holder.GymViewHolder;

import java.util.ArrayList;
import java.util.List;

public class GymListAdapter extends RecyclerView.Adapter<GymViewHolder> {

    private OnFavoriteListener mListener;
    private List<FFGym> mGyms;
    private GymDatabase mGymDatabase;

    public GymListAdapter(OnFavoriteListener listener) {
        mListener = listener;
        mGyms = new ArrayList<>();
    }

    public GymListAdapter(List<FFGym> gyms, OnFavoriteListener listener) {
        mGyms = gyms;
        mListener = listener;
    }

    public void setGyms(List<FFGym> gyms) {
        mGyms = gyms;
        notifyDataSetChanged();
    }

    public void clear() {
        mGyms.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mGyms.size();
    }

    public FFGym getItem(int position) {
        return mGyms.get(position);
    }

    public void removeItem(int position) {
        mGyms.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public GymViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GymViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gym_list_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(GymViewHolder holder, int position) {
        if (mGymDatabase == null) {
            mGymDatabase = new GymDatabase(holder.itemView.getContext());
        }
        holder.populate(mGymDatabase, getItem(position));
    }

    public interface OnFavoriteListener {
        void onFavorite(int position, boolean isFavorite);
    }
}
