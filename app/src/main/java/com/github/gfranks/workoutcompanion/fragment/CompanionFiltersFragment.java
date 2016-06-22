package com.github.gfranks.workoutcompanion.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.FragmentActivity;
import com.github.gfranks.workoutcompanion.adapter.SearchSuggestionsAdapter;
import com.github.gfranks.workoutcompanion.data.api.GoogleApiService;
import com.github.gfranks.workoutcompanion.data.model.WCCompanionFilters;
import com.github.gfranks.workoutcompanion.data.model.WCErrorResponse;
import com.github.gfranks.workoutcompanion.data.model.WCLocation;
import com.github.gfranks.workoutcompanion.data.model.WCLocations;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.manager.FilterManager;
import com.github.gfranks.workoutcompanion.manager.GoogleApiManager;

import javax.inject.Inject;

import butterknife.InjectView;

public class CompanionFiltersFragment extends BaseFragment implements SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener {

    public static final String TAG = "companion_filters_fragment";

    @Inject
    FilterManager mFilterManager;
    @Inject
    GoogleApiManager mGoogleApiManager;
    @Inject
    GoogleApiService mGoogleApiService;

    @InjectView(R.id.filter_location)
    SearchView mSearchView;

    private WCCompanionFilters mFilterOptions;
    private boolean mFiltersChanged;
    private SearchSuggestionsAdapter mSearchViewAdapter;

    public static CompanionFiltersFragment newInstance() {
        return new CompanionFiltersFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFilterOptions = mFilterManager.getFilterOptions();
        mSearchViewAdapter = new SearchSuggestionsAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_companion_filters, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
        mSearchView.setSuggestionsAdapter(mSearchViewAdapter);
        initFilterOptions();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.nav_companion_filters);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_companion_filters, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_save).setVisible(mFiltersChanged);
        menu.findItem(R.id.action_cancel).setVisible(mFiltersChanged);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
            case R.id.action_cancel:
                initFilterOptions();
                setFiltersChanged(false);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                        WCLocation location = msg.getData().getParcelable(WCLocation.EXTRA);
                        mSearchView.setQuery(location.getFormatted_address(), false);
                        mFilterOptions.setLocation(location);
                        setFiltersChanged(true);
                        break;
                    case GoogleApiManager.STATUS_FAILURE:
                        if (!isDetached() && getActivity() != null) {
                            Throwable t = msg.getData().getParcelable(WCErrorResponse.EXTRA);
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
                            mSearchViewAdapter.updateWithLocationResults(((WCLocations) msg.getData().getParcelable(WCLocations.EXTRA)).getResults());
                            break;
                        case GoogleApiManager.STATUS_FAILURE:
                            if (!isDetached() && getActivity() != null) {
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
        WCLocation location = mSearchViewAdapter.getResultItem(position);
        mSearchView.setQuery(location.getFormatted_address(), false);
        mFilterOptions.setLocation(location);
        setFiltersChanged(true);
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    private void initFilterOptions() {
        mSearchView.setQuery(mFilterOptions.getLocation().getFormatted_address(), false);
    }

    private void setFiltersChanged(boolean changed) {
        if (mFiltersChanged != changed) {
            mFiltersChanged = changed;
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    private void save() {
        setFiltersChanged(false);
        mFilterManager.setFilterOptions(mFilterOptions);
        GFMinimalNotification notification = GFMinimalNotification.make(getView(), R.string.filters_saved, GFMinimalNotification.LENGTH_LONG);
        if (getActivity() instanceof FragmentActivity) {
            getActivity().setResult(Activity.RESULT_OK); // notify the caller activity that filters have been changed
            notification.setCallback(new GFMinimalNotification.Callback() {
                @Override
                public void onDismissed(GFMinimalNotification notification, int event) {
                    getActivity().supportFinishAfterTransition();
                }
            }).show();
        }
        notification.show();
    }
}
