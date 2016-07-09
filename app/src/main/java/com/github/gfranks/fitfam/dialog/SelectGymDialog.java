package com.github.gfranks.fitfam.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SearchView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.gfranks.fitfam.data.api.FitFamService;
import com.github.gfranks.fitfam.data.model.FFGym;
import com.github.gfranks.fitfam.data.model.FFGyms;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.adapter.GymListAdapter;
import com.github.gfranks.fitfam.adapter.SearchSuggestionsAdapter;
import com.github.gfranks.fitfam.application.FitFamApplication;
import com.github.gfranks.fitfam.data.api.GoogleApiService;
import com.github.gfranks.fitfam.data.model.FFErrorResponse;
import com.github.gfranks.fitfam.data.model.FFLocation;
import com.github.gfranks.fitfam.data.model.FFLocations;
import com.github.gfranks.fitfam.data.model.FFUser;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.manager.GoogleApiManager;
import com.github.gfranks.fitfam.util.GymDatabase;
import com.github.gfranks.fitfam.view.FFEmptyView;
import com.github.gfranks.fitfam.view.FFRecyclerView;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectGymDialog extends MaterialDialog implements SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener, FFRecyclerView.OnItemClickListener, GymListAdapter.OnFavoriteListener {

    @Inject
    GoogleApiManager mGoogleApiManager;
    @Inject
    GoogleApiService mGoogleApiService;
    @Inject
    FitFamService mService;
    @Inject
    AccountManager mAccountManager;

    @InjectView(R.id.search_view_select_gym)
    SearchView mSearchView;
    @InjectView(R.id.select_gym_list)
    FFRecyclerView mListView;
    @InjectView(R.id.list_empty_text)
    FFEmptyView mEmptyView;

    private FFUser mUser;
    private SearchSuggestionsAdapter mSearchViewAdapter;
    private GymListAdapter mAdapter;
    private OnGymSelectedListener mOnGymSelectedListener;
    private GymDatabase mGymDatabase;

    private SelectGymDialog(Context context, boolean isSelectingHomeGym) {
        super(getBuilder(context, isSelectingHomeGym));
        FitFamApplication.get(context).inject(this);
        mSearchViewAdapter = new SearchSuggestionsAdapter(getContext());
        mGymDatabase = new GymDatabase(context);
        setupViews();
    }

    public static SelectGymDialog newInstance(Context context, boolean isSelectingHomeGym, OnGymSelectedListener onGymSelectedListener) {
        SelectGymDialog dialog = new SelectGymDialog(context, isSelectingHomeGym);
        dialog.setOnGymSelectedListener(onGymSelectedListener);
        return dialog;
    }

    public static SelectGymDialog newInstance(Context context, boolean isSelectingHomeGym, FFLocation location, OnGymSelectedListener onGymSelectedListener) {
        SelectGymDialog dialog = new SelectGymDialog(context, isSelectingHomeGym);
        dialog.mOnGymSelectedListener = onGymSelectedListener;

        if (location != null) {
            dialog.setupWithLocation(location);
        }

        return dialog;
    }

    private static MaterialDialog.Builder getBuilder(Context context, boolean isSelectingHomeGym) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_gym, null);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .negativeText(R.string.action_cancel)
                .title(isSelectingHomeGym ? R.string.home_gym_select : R.string.gym_select)
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

    private void setupWithLocation(FFLocation location) {
        mSearchView.setVisibility(View.GONE);
        loadGyms(location.getPosition());
    }

    /**
     * ******************************
     * SearchView.OnQueryTextListener
     * ******************************
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        mGoogleApiManager.getLocationFromQuery(query, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case GoogleApiManager.STATUS_SUCCESS:
                        FFLocation location = msg.getData().getParcelable(FFLocation.EXTRA);
                        mSearchView.setQuery(location.getFormatted_address(), false);
                        loadGyms(location.getPosition());
                        break;
                    case GoogleApiManager.STATUS_FAILURE:
                        if (isShowing()) {
                            Throwable t = msg.getData().getParcelable(FFErrorResponse.EXTRA);
                            GFMinimalNotification.make(getView(), t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
                        }
                        break;
                }
                return false;
            }
        }));
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
                            mSearchViewAdapter.updateWithLocationResults(((FFLocations) msg.getData().getParcelable(FFLocations.EXTRA)).getResults());
                            break;
                        case GoogleApiManager.STATUS_FAILURE:
                            if (isShowing()) {
                                Throwable t = msg.getData().getParcelable(FFErrorResponse.EXTRA);
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
     * FFRecyclerView.OnItemClickListener
     * **********************************
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, int position) {
        if (mOnGymSelectedListener != null) {
            FFGym result = mAdapter.getItem(position);
            mOnGymSelectedListener.onGymSelected(this, result);
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

    public void setOnGymSelectedListener(OnGymSelectedListener onGymSelectedListener) {
        mOnGymSelectedListener = onGymSelectedListener;
    }

    private void loadGyms(LatLng latLng) {
        mEmptyView.displayLoading(true);
        mGoogleApiService.getGyms(latLng.latitude + "," + latLng.longitude, getContext().getString(R.string.api_places_key)).enqueue(new Callback<FFGyms>() {
            @Override
            public void onResponse(Call<FFGyms> call, Response<FFGyms> response) {
                if (!isShowing()) {
                    return;
                }

                mEmptyView.displayLoading(false);
                if (mAdapter == null || mListView.getAdapter() == null) {
                    mAdapter = new GymListAdapter(response.body().getResults(), SelectGymDialog.this);
                    mListView.setAdapter(mAdapter);
                } else {
                    mAdapter.setGyms(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<FFGyms> call, Throwable t) {
                if (!isShowing()) {
                    return;
                }
                mEmptyView.displayLoading(false);
                GFMinimalNotification.make(getView(), t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
            }
        });
    }

    public interface OnGymSelectedListener {
        void onGymSelected(SelectGymDialog dialog, FFGym gym);
    }
}