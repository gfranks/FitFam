package com.github.gfranks.workoutcompanion.dialog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.adapter.GymListAdapter;
import com.github.gfranks.workoutcompanion.adapter.SearchSuggestionsAdapter;
import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;
import com.github.gfranks.workoutcompanion.data.api.DiscoverService;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.model.WCGyms;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.data.model.WCLocations;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.notification.WCInAppMessageManagerConstants;
import com.github.gfranks.workoutcompanion.view.EmptyView;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;
import com.google.android.gms.maps.model.LatLng;
import com.urbanairship.UAirship;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectGymDialog extends MaterialDialog implements SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener, WCRecyclerView.OnItemClickListener {

    @Inject
    DiscoverService mDiscoverService;
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
    private GymListAdapter mListViewAdapter;
    private OnGymSelectedListener mOnGymSelectedListener;

    public static SelectGymDialog newInstance(Context context, OnGymSelectedListener onGymSelectedListener) {
        return new SelectGymDialog(context, onGymSelectedListener);
    }

    private SelectGymDialog(Context context, OnGymSelectedListener onGymSelectedListener) {
        super(getBuilder(context));
        WorkoutCompanionApplication.get(context).inject(this);
        mOnGymSelectedListener = onGymSelectedListener;
        mSearchViewAdapter = new SearchSuggestionsAdapter(getContext());
        setupViews();
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
    public boolean onQueryTextChange(String address) {
        if (address.length() == 3) {
            mDiscoverService.getLocations(address, getContext().getString(R.string.api_places_key)).enqueue(new Callback<WCLocations>() {
                @Override
                public void onResponse(Call<WCLocations> call, Response<WCLocations> response) {
                    if (!isShowing()) {
                        return;
                    }
                    mSearchViewAdapter.updateWithLocationResults(response.body().getResults());
                }

                @Override
                public void onFailure(Call<WCLocations> call, Throwable t) {
                    if (!isShowing()) {
                        return;
                    }
                    UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getErrorBuilder()
                            .setAlert(t.getMessage())
                            .create());
                }
            });
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
            WCGym result = mListViewAdapter.getItem(position);
            mOnGymSelectedListener.onGymSelected(this, result.getPlace_id(), result.getName());
        }
        dismiss();
    }

    private void loadGyms(LatLng latLng) {
        mEmptyView.displayLoading(true);
        mDiscoverService.getGyms(latLng.latitude + "," + latLng.longitude, getContext().getString(R.string.api_places_key)).enqueue(new Callback<WCGyms>() {
            @Override
            public void onResponse(Call<WCGyms> call, Response<WCGyms> response) {
                if (!isShowing()) {
                    return;
                }

                mEmptyView.displayLoading(false);
                if (mListViewAdapter == null || mListView.getAdapter() == null) {
                    mListViewAdapter = new GymListAdapter(response.body().getResults());
                    mListView.setAdapter(mListViewAdapter);
                } else {
                    mListViewAdapter.setGyms(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<WCGyms> call, Throwable t) {
                if (!isShowing()) {
                    return;
                }
                mEmptyView.displayLoading(false);
                UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getErrorBuilder()
                        .setAlert(t.getMessage())
                        .create());
            }
        });
    }

    public interface OnGymSelectedListener {
        void onGymSelected(SelectGymDialog dialog, String placeId, String gym);
    }
}