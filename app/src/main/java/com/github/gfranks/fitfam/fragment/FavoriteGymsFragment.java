package com.github.gfranks.fitfam.fragment;

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

import com.github.gfranks.fitfam.activity.GymDetailsActivity;
import com.github.gfranks.fitfam.adapter.GymListAdapter;
import com.github.gfranks.fitfam.adapter.holder.GymViewHolder;
import com.github.gfranks.fitfam.data.model.FFGym;
import com.github.gfranks.fitfam.fragment.base.BaseFragment;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.util.GymDatabase;
import com.github.gfranks.fitfam.view.FFEmptyView;
import com.github.gfranks.fitfam.view.FFRecyclerView;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.fitfam.R;

import javax.inject.Inject;

import butterknife.InjectView;

public class FavoriteGymsFragment extends BaseFragment implements FFRecyclerView.OnItemClickListener, GymListAdapter.OnFavoriteListener {

    public static final String TAG = "favorite_gyms_fragment";

    @Inject
    AccountManager mAccountManager;

    @InjectView(R.id.list)
    FFRecyclerView mListView;
    @InjectView(R.id.list_empty_text)
    FFEmptyView mEmptyView;

    private GymListAdapter mAdapter;
    private GymDatabase mGymDatabase;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GymDatabase.BROADCAST)) {
                loadGyms();
            }
        }
    };

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
     * FFRecyclerView.OnItemClickListener
     * **********************************
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, int position) {
        Intent intent = new Intent(getActivity(), GymDetailsActivity.class);
        intent.putExtra(FFGym.EXTRA, mAdapter.getItem(position));
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
            GFMinimalNotification.make(getView(), R.string.gym_unfavorited, GFMinimalNotification.LENGTH_LONG).show();
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
            GFMinimalNotification.make(getView(), R.string.error_unable_to_load_saved_gyms, GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
        }
    }

    private void setupEmptyView() {
        mEmptyView.setTitle(R.string.empty_favorite_gyms_title);
        mEmptyView.setSubtitle(R.string.empty_favorite_gyms_subtitle);
        View emptyHeader = mEmptyView.addEmptyHeader(R.layout.layout_gym_list_item);
        emptyHeader.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme_background_dark));
        new GymViewHolder(emptyHeader, null).populateAsPlaceHolder(new FFGym.Builder()
                .setName(getString(R.string.empty_favorite_gyms_header_name))
                .setIcon(getString(R.string.empty_favorite_gyms_header_image))
                .setVicinity(getString(R.string.empty_favorite_gyms_header_vicinity))
                .setRating(4.3f)
                .build());
        mListView.setEmptyView(mEmptyView);
    }
}
