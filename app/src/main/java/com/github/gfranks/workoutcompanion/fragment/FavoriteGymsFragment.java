package com.github.gfranks.workoutcompanion.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.GymDetailsActivity;
import com.github.gfranks.workoutcompanion.adapter.GymListAdapter;
import com.github.gfranks.workoutcompanion.adapter.holder.GymViewHolder;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.notification.WCInAppMessageManagerConstants;
import com.github.gfranks.workoutcompanion.util.GymDatabase;
import com.github.gfranks.workoutcompanion.view.EmptyView;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;
import com.urbanairship.UAirship;

import javax.inject.Inject;

import butterknife.InjectView;

public class FavoriteGymsFragment extends BaseFragment implements WCRecyclerView.OnItemClickListener, GymListAdapter.OnFavoriteListener {

    public static final String TAG = "favorite_gyms_fragment";

    @Inject
    AccountManager mAccountManager;

    @InjectView(R.id.list)
    WCRecyclerView mListView;
    @InjectView(R.id.list_empty_text)
    EmptyView mEmptyView;

    private GymListAdapter mAdapter;
    private GymDatabase mGymDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGymDatabase = new GymDatabase(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_gyms, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupEmptyView();
        mListView.setOnItemClickListener(this);
        mListView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.recycler_view_layout_animation));
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter(GymDatabase.BROADCAST));
        loadGyms();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        mGymDatabase.close();
    }

    /**
     * **********************************
     * WCRecyclerView.OnItemClickListener
     * **********************************
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, int position) {
        Intent intent = new Intent(getActivity(), GymDetailsActivity.class);
        intent.putExtra(WCGym.EXTRA, mAdapter.getItem(position));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(((GymViewHolder) vh).getNameViewForTransition(), getString(R.string.transition_gym_name)),
                    Pair.create(((GymViewHolder) vh).getAddressViewForTransition(), getString(R.string.transition_gym_address)),
                    Pair.create(((GymViewHolder) vh).getFavoriteViewForTransition(), getString(R.string.transition_gym_favorite)));
            getActivity().startActivity(intent, options.toBundle());
        } else {
            getActivity().startActivity(intent);
        }
    }

    /**
     * **********************************
     * GymListAdapter.OnFavoritedListener
     * **********************************
     */
    @Override
    public void onFavorite(int position, boolean isFavorite) {
        try {
            mGymDatabase.open();
            mGymDatabase.deleteGym(mAccountManager.getUser().getId(), mAdapter.getItem(position).getId());
            UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getSuccessBuilder()
                    .setAlert(getString(R.string.gym_unfavorited))
                    .create());
            mAdapter.removeItem(position);
            mGymDatabase.close();
        } catch (Throwable t) {
            // unable to open db
        }
    }

    private void loadGyms() {
        if (isDetached() || getActivity() == null) {
            return;
        }

        try {
            mEmptyView.displayLoading(true);
            mGymDatabase.open();
            if (mAdapter == null || mListView.getAdapter() == null) {
                mAdapter = new GymListAdapter(mGymDatabase.getAllGyms(mAccountManager.getUser().getId()), this);
                mListView.setAdapter(mAdapter);
            } else {
                mAdapter.setGyms(mGymDatabase.getAllGyms(mAccountManager.getUser().getId()));
            }
            mEmptyView.displayLoading(false);
        } catch (Throwable t) {
            mEmptyView.displayLoading(false);
            UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getErrorBuilder()
                    .setAlert(getString(R.string.error_unable_to_load_saved_gyms))
                    .create());
        }
    }

    private void setupEmptyView() {
        mEmptyView.setTitle(R.string.empty_favorite_gyms_title);
        mEmptyView.setSubtitle(R.string.empty_favorite_gyms_subtitle);
        View emptyHeader = mEmptyView.addEmptyHeader(R.layout.layout_gym_list_item);
        emptyHeader.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme_background_dark));
        new GymViewHolder(emptyHeader, null).populateAsPlaceHolder(new WCGym.Builder()
                .setName(getString(R.string.empty_favorite_gyms_header_name))
                .setIcon(getString(R.string.empty_favorite_gyms_header_image))
                .setVicinity(getString(R.string.empty_favorite_gyms_header_vicinity))
                .build());
        mListView.setEmptyView(mEmptyView);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GymDatabase.BROADCAST)) {
                loadGyms();
            }
        }
    };
}
