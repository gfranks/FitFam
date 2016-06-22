package com.github.gfranks.workoutcompanion.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.adapter.GymListAdapter;
import com.github.gfranks.workoutcompanion.adapter.SearchSuggestionsAdapter;
import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;
import com.github.gfranks.workoutcompanion.data.api.GoogleApiService;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.model.WCErrorResponse;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.data.model.WCGyms;
import com.github.gfranks.workoutcompanion.data.model.WCLocations;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.manager.GoogleApiManager;
import com.github.gfranks.workoutcompanion.util.GymDatabase;
import com.github.gfranks.workoutcompanion.view.EmptyView;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectGymDialog extends MaterialDialog implements SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener, WCRecyclerView.OnItemClickListener, GymListAdapter.OnFavoriteListener {

    @Inject
    GoogleApiManager mGoogleApiManager;
    @Inject
    GoogleApiService mGoogleApiService;
    @Inject
    WorkoutCompanionService mService;
    @Inject
    AccountManager mAccountManager;

    @InjectView(R.id.search_view_select_gym)
    SearchView mSearchView;
    @InjectView(R.id.select_gym_list)
    WCRecyclerView mListView;
    @InjectView(R.id.list_empty_text)
    EmptyView mEmptyView;

    private WCUser mUser;
    private SearchSuggestionsAdapter mSearchViewAdapter;
    private GymListAdapter mAdapter;
    private OnGymSelectedListener mOnGymSelectedListener;
    private GymDatabase mGymDatabase;

    private SelectGymDialog(Context context, OnGymSelectedListener onGymSelectedListener) {
        super(getBuilder(context));
        WorkoutCompanionApplication.get(context).inject(this);
        mOnGymSelectedListener = onGymSelectedListener;
        mSearchViewAdapter = new SearchSuggestionsAdapter(getContext());
        mGymDatabase = new GymDatabase(context);
        setupViews();
    }

    public static SelectGymDialog newInstance(Context context, OnGymSelectedListener onGymSelectedListener) {
        return new SelectGymDialog(context, onGymSelectedListener);
    }

    private static MaterialDialog.Builder getBuilder(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_gym, null);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .negativeText(R.string.action_cancel)
                .title("Select Home Gym")
                .customView(view, false);

        return builder;
    }

    private void setupViews() {
        mUser = mAccountManager.getUser();
        ButterKnife.inject(this, getCustomView());
        mSearchView.setSuggestionsAdapter(mSearchViewAdapter);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
        mEmptyView.setSubtitle(R.string.empty_select_home_gym);
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(mEmptyView);
    }

    /**
     * ******************************
     * SearchView.OnQueryTextListener
     * ******************************
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query.length() >= 3) {
            mGoogleApiManager.getLocationsFromQuery(query, new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what) {
                        case GoogleApiManager.STATUS_SUCCESS:
                            mSearchViewAdapter.updateWithLocationResults(((WCLocations) msg.getData().getParcelable(WCLocations.EXTRA)).getResults());
                            break;
                        case GoogleApiManager.STATUS_FAILURE:
                            if (isShowing()) {
                                Throwable t = msg.getData().getParcelable(WCErrorResponse.EXTRA);
                                GFMinimalNotification.make(getView(), t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
                            }
                            break;
                    }
                    return true;
                }
            }));
        }
        return false;
    }

    /**
     * *******************************
     * SearchView.OnSuggestionListener
     * *******************************
     */
    @Override
    public boolean onSuggestionClick(int position) {
        mSearchView.setQuery(mSearchViewAdapter.getResultItem(position).getFormatted_address(), false);
        loadGyms(mSearchViewAdapter.getResultItem(position).getPosition());
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    /**
     * **********************************
     * WCRecyclerView.OnItemClickListener
     * **********************************
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, int position) {
        if (mOnGymSelectedListener != null) {
            WCGym result = mAdapter.getItem(position);
            mOnGymSelectedListener.onGymSelected(this, result.getPlace_id(), result.getName());
        }
        dismiss();
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
            if (isFavorite) {
                mGymDatabase.saveGym(mAccountManager.getUser().getId(), mAdapter.getItem(position));
                GFMinimalNotification.make(getView(), R.string.gym_favorited, GFMinimalNotification.LENGTH_LONG).show();
            } else {
                mGymDatabase.deleteGym(mAccountManager.getUser().getId(), mAdapter.getItem(position).getId());
                GFMinimalNotification.make(getView(), R.string.gym_unfavorited, GFMinimalNotification.LENGTH_LONG).show();
            }
            mGymDatabase.close();
        } catch (Throwable t) {
            // unable to open db
        }
    }

    private void loadGyms(LatLng latLng) {
        mEmptyView.displayLoading(true);
        mGoogleApiService.getGyms(latLng.latitude + "," + latLng.longitude, getContext().getString(R.string.api_places_key)).enqueue(new Callback<WCGyms>() {
            @Override
            public void onResponse(Call<WCGyms> call, Response<WCGyms> response) {
                if (!isShowing()) {
                    return;
                }

                mEmptyView.displayLoading(false);
                if (mAdapter == null || mListView.getAdapter() == null) {
                    mAdapter = new GymListAdapter(response.body().getResults(),SelectGymDialog. this);
                    mListView.setAdapter(mAdapter);
                } else {
                    mAdapter.setGyms(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<WCGyms> call, Throwable t) {
                if (!isShowing()) {
                    return;
                }
                mEmptyView.displayLoading(false);
                GFMinimalNotification.make(getView(), t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
            }
        });
    }

    public interface OnGymSelectedListener {
        void onGymSelected(SelectGymDialog dialog, String placeId, String gym);
    }
}