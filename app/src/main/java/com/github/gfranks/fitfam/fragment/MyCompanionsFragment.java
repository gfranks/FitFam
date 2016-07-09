package com.github.gfranks.fitfam.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.gfranks.fitfam.activity.UserProfileActivity;
import com.github.gfranks.fitfam.adapter.UserListAdapter;
import com.github.gfranks.fitfam.adapter.holder.UserViewHolder;
import com.github.gfranks.fitfam.data.api.FitFamService;
import com.github.gfranks.fitfam.data.model.FFUser;
import com.github.gfranks.fitfam.fragment.base.BaseFragment;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.view.FFEmptyView;
import com.github.gfranks.fitfam.view.FFRecyclerView;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.fitfam.R;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCompanionsFragment extends BaseFragment implements Callback<List<FFUser>>, FFRecyclerView.OnItemClickListener {

    public static final String TAG = "my_companions_fragment";

    @Inject
    FitFamService mService;
    @Inject
    AccountManager mAccountManager;

    @InjectView(R.id.list)
    FFRecyclerView mListView;
    @InjectView(R.id.list_empty_text)
    FFEmptyView mEmptyView;

    private UserListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_companions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupEmptyView();
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mEmptyView.displayLoading(true);
        mService.getRecentCompanions(mAccountManager.getUser().getId()).enqueue(this);
    }

    /**
     * ****************
     * Callback<FFUser>
     * ****************
     */
    @Override
    public void onResponse(Call<List<FFUser>> call, Response<List<FFUser>> response) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        mEmptyView.displayLoading(false);
        if (mAdapter == null) {
            mAdapter = new UserListAdapter(response.body());
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.setUsers(response.body());
        }
    }

    @Override
    public void onFailure(Call<List<FFUser>> call, Throwable t) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        mEmptyView.displayLoading(false);
        GFMinimalNotification.make(getView(), t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
    }

    /**
     * **********************************
     * FFRecyclerView.OnItemClickListener
     * **********************************
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, int position) {
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        intent.putExtra(FFUser.EXTRA, mAdapter.getItem(position));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    ((UserViewHolder) vh).getImageViewForTransition(), getString(R.string.transition_user_image));
            getActivity().startActivity(intent, options.toBundle());
        } else {
            getActivity().startActivity(intent);
        }
    }

    private void setupEmptyView() {
        mEmptyView.setTitle(R.string.empty_workout_companions_title);
        mEmptyView.setSubtitle(R.string.empty_workout_companions_subtitle);
        View emptyHeader = mEmptyView.addEmptyHeader(R.layout.layout_user_list_item);
        emptyHeader.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme_background_dark));
        new UserViewHolder(emptyHeader).populateAsPlaceHolder(new FFUser.Builder()
                .setFirstName(getString(R.string.empty_companions_header_first_name))
                .setLastName(getString(R.string.empty_companions_header_last_name))
                .setExercises(Arrays.asList(getString(R.string.empty_companions_header_exercises).split(", ")))
                .setImage(getString(R.string.empty_companions_header_image))
                .build());
        mListView.setEmptyView(mEmptyView);
    }
}
