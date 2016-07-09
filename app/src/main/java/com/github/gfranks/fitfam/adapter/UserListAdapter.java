package com.github.gfranks.fitfam.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.gfranks.fitfam.adapter.holder.UserViewHolder;
import com.github.gfranks.fitfam.data.model.FFUser;
import com.github.gfranks.fitfam.R;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private List<FFUser> mUsers;

    public UserListAdapter() {
        mUsers = new ArrayList<>();
    }

    public UserListAdapter(List<FFUser> users) {
        mUsers = users;
    }

    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    public void setUsers(List<FFUser> users) {
        mUsers = users;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public FFUser getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.populate(getItem(position));
    }
}
