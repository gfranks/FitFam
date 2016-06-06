package com.github.gfranks.workoutcompanion.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.UserProfileActivity;
import com.github.gfranks.workoutcompanion.adapter.UserListAdapter;
import com.github.gfranks.workoutcompanion.adapter.holder.UserViewHolder;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.notification.WCInAppMessageManagerConstants;
import com.github.gfranks.workoutcompanion.view.EmptyView;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;
import com.urbanairship.UAirship;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCompanionsFragment extends BaseFragment implements WCRecyclerView.OnItemClickListener, Callback<List<WCUser>> {

    public static final String TAG = "my_companions_fragment";

    @Inject
    WorkoutCompanionService mService;
    @Inject
    AccountManager mAccountManager;

    @InjectView(R.id.list)
    WCRecyclerView mListView;
    @InjectView(R.id.list_empty_text)
    EmptyView mEmptyView;

    private UserListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_companions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmptyView.setText(R.string.empty_workout_companions);
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(this);
        mListView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.recycler_view_layout_animation));
    }

    @Override
    public void onResume() {
        super.onResume();
        mEmptyView.displayLoading(true);
        mService.getRecentCompanions(mAccountManager.getUser().getId()).enqueue(this);
    }

    /**
     * **********************************
     * WCRecyclerView.OnItemClickListener
     * **********************************
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, int position) {
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        intent.putExtra(WCUser.EXTRA, mAdapter.getItem(position));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    ((UserViewHolder) vh).getImageViewForTransition(), getString(R.string.transition_image));
            getActivity().startActivity(intent, options.toBundle());
        } else {
            getActivity().startActivity(intent);
        }
    }

    /**
     * ****************
     * Callback<WCUser>
     * ****************
     */
    @Override
    public void onResponse(Call<List<WCUser>> call, Response<List<WCUser>> response) {
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
    public void onFailure(Call<List<WCUser>> call, Throwable t) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        mEmptyView.displayLoading(false);
        UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getErrorBuilder()
                .setAlert(t.getMessage())
                .create());
    }
}
