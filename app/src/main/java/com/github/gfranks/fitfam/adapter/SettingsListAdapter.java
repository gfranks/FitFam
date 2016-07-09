package com.github.gfranks.fitfam.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.gfranks.fitfam.util.SettingsItem;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.adapter.holder.SettingsViewHolder;

public class SettingsListAdapter extends RecyclerView.Adapter<SettingsViewHolder> {

    private OnSettingsItemClickListener mOnQuietTimeChangeListener;

    public SettingsListAdapter(OnSettingsItemClickListener listener) {
        mOnQuietTimeChangeListener = listener;
    }

    @Override
    public int getItemCount() {
        return SettingsItem.values().length;
    }

    public SettingsItem getItem(int position) {
        return SettingsItem.values()[position];
    }

    @Override
    public SettingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SettingsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_settings_list_item, parent, false),
                mOnQuietTimeChangeListener);
    }

    @Override
    public void onBindViewHolder(SettingsViewHolder holder, int position) {
        holder.populate(getItem(position));
    }

    public interface OnSettingsItemClickListener {
        void onChangeQuietTimeStart();

        void onChangeQuietTimeEnd();
    }
}