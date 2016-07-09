package com.github.gfranks.fitfam.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import com.github.gfranks.fitfam.activity.GymDetailsActivity;
import com.github.gfranks.fitfam.adapter.DiscoverMapRenderer;
import com.github.gfranks.fitfam.adapter.GymListAdapter;
import com.github.gfranks.fitfam.adapter.SearchSuggestionsAdapter;
import com.github.gfranks.fitfam.adapter.holder.GymViewHolder;
import com.github.gfranks.fitfam.data.api.GoogleApiService;
import com.github.gfranks.fitfam.data.model.FFGym;
import com.github.gfranks.fitfam.data.model.FFGyms;
import com.github.gfranks.fitfam.fragment.base.BaseFragment;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.util.GymDatabase;
import com.github.gfranks.fitfam.view.FFEmptyView;
import com.github.gfranks.fitfam.view.FFRecyclerView;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.data.model.FFErrorResponse;
import com.github.gfranks.fitfam.data.model.FFLocations;
import com.github.gfranks.fitfam.manager.GoogleApiManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverMapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ClusterManager.OnClusterClickListener<FFGym>, ClusterManager.OnClusterItemClickListener<FFGym>,
        GoogleMap.OnMapClickListener, FFRecyclerView.OnItemClickListener, SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener, GymListAdapter.OnFavoriteListener {

    public static final String TAG = "discover_map_fragment";

    @Inject
    GoogleApiManager mGoogleApiManager;
    @Inject
    GoogleApiService mGoogleApiService;
    @Inject
    AccountManager mAccountManager;

    @InjectView(R.id.map_view)
    MapView mMapView;
    @InjectView(R.id.map_view_list_container)
    View mBottomSheet;
    @InjectView(R.id.map_view_list_top_shadow)
    View mListViewTopShadow;
    @InjectView(R.id.map_view_list)
    FFRecyclerView mListView;
    @InjectView(R.id.list_empty_text)
    FFEmptyView mEmptyView;

    private GoogleMap mMap;
    private ClusterManager<FFGym> mClusterManager;
    private List<FFGym> mGyms;
    private GymListAdapter mAdapter;
    private SearchView mSearchView;
    private SearchSuggestionsAdapter mSearchViewAdapter;
    private GymDatabase mGymDatabase;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GymDatabase.BROADCAST)) {
                try {
                    mAdapter.notifyDataSetChanged();
                } catch (Throwable t) {
                    // do nothing
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSearchViewAdapter = new SearchSuggestionsAdapter(getContext());
        mGymDatabase = new GymDatabase(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.onCreate(null);
        mEmptyView.setSubtitle(R.string.empty_gym);
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter(GymDatabase.BROADCAST));
        mMapView.onResume();

        if (getMap() == null) {
            mMapView.getMapAsync(this);
        } else if (!mGoogleApiManager.isConnected()) {
            mGoogleApiManager.connect(this, this, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGoogleApiManager.disconnect();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_discover_map, menu);

        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
        mSearchView.setSuggestionsAdapter(mSearchViewAdapter);
        try {
            int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
            EditText searchEditText = (EditText) mSearchView.findViewById(searchSrcTextId);
            searchEditText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.white_translucent));
        } catch (Throwable t) {}
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (isListShown()) {
            menu.findItem(R.id.action_show_list).setTitle(R.string.action_hide_list);
        } else {
            menu.findItem(R.id.action_show_list).setTitle(R.string.action_show_list);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_list) {
            if (isListShown()) {
                hideList();
            } else if (mGyms != null) {
                showList(mGyms, null, true);
            } else {
                GFMinimalNotification.make(getView(), R.string.error_unable_to_show_list,
                        GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_DEFAULT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GoogleApiManager.REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getMap().setMyLocationEnabled(true); // no need to check permission, we have been granted access
            mGoogleApiManager.connect(this, this, this);
        }
    }

    /**
     * ******************
     * OnMapReadyCallback
     * ******************
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (mClusterManager == null) {
            mClusterManager = new ClusterManager<>(getContext(), getMap());
            mClusterManager.setRenderer(new DiscoverMapRenderer(getContext(), getMap(), mClusterManager));
            mClusterManager.setOnClusterClickListener(this);
            mClusterManager.setOnClusterItemClickListener(this);
        }
        getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnMapClickListener(this);
        if (mGoogleApiManager.connect(this, this, this)) {
            getMap().setMyLocationEnabled(true); // no need to check permission, we have been granted access
        }
        getMap().setIndoorEnabled(true);
        getMap().setBuildingsEnabled(true);
        getMap().getUiSettings().setZoomControlsEnabled(true);
    }

    /**
     * *************************************
     * ClusterManager.OnClusterClickListener
     * *************************************
     */
    @Override
    public boolean onClusterClick(final Cluster<FFGym> cluster) {
        if (cluster.getSize() > 5) {
            LatLngBounds.Builder builder = LatLngBounds.builder();
            for (ClusterItem item : cluster.getItems()) {
                builder.include(item.getPosition());
            }
            final LatLngBounds bounds = builder.build();
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    if (mListView.getVisibility() == View.VISIBLE) {
                        ensureSelectedMarkerVisible(cluster.getPosition());
                    }
                    mClusterManager.cluster();
                }

                @Override
                public void onCancel() {
                }
            });
            return true;
        } else {
            List<FFGym> results = new ArrayList<>();
            results.addAll(cluster.getItems());
            showList(results, cluster.getPosition(), false);
            return false;
        }
    }

    /**
     * *****************************************
     * ClusterManager.OnClusterItemClickListener
     * *****************************************
     */
    @Override
    public boolean onClusterItemClick(final FFGym ffGym) {
        List<FFGym> results = new ArrayList<>();
        results.add(ffGym);
        showList(results, ffGym.getPosition(), false);
        return false;
    }

    /**
     * ****************************
     * GoogleMap.OnMapClickListener
     * ****************************
     */
    @Override
    public void onMapClick(LatLng latLng) {
        hideList();
    }

    /**
     * ***********************************
     * GoogleApiClient.ConnectionCallbacks
     * ***********************************
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        loadGyms();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    /**
     * ******************************************
     * GoogleApiClient.OnConnectionFailedListener
     * ******************************************
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        GFMinimalNotification.make(getView(), R.string.error_no_location, GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
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
     * ******************************
     * SearchView.OnQueryTextListener
     * ******************************
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        mGoogleApiManager.setLastLocationFromQuery(query, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == GoogleApiManager.STATUS_SUCCESS) {
                    loadGyms();
                } else {
                    if (!isDetached() && getActivity() != null) {
                        Throwable t = msg.getData().getParcelable(FFErrorResponse.EXTRA);
                        GFMinimalNotification.make(getView(), t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
                    }
                }
                return true;
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
                            if (!isDetached() && getActivity() != null) {
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
        mGoogleApiManager.setLastKnownLocation(mSearchViewAdapter.getResultItem(position).getPosition());
        getActivity().supportInvalidateOptionsMenu();
        loadGyms();
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
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

    private void loadGyms() {
        LatLng latLng = mGoogleApiManager.getLastKnownLocation();
        mGoogleApiService.getGyms(latLng.latitude + "," + latLng.longitude, getString(R.string.api_places_key)).enqueue(new Callback<FFGyms>() {
            @Override
            public void onResponse(Call<FFGyms> call, Response<FFGyms> response) {
                if (isDetached() || getActivity() == null) {
                    return;
                }
                mGyms = response.body().getResults();
                mClusterManager.clearItems();
                mClusterManager.addItems(mGyms);
                if (mGyms.size() > 0) {
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    for (ClusterItem item : mGyms) {
                        builder.include(item.getPosition());
                    }
                    final LatLngBounds bounds = builder.build();
                    getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            mClusterManager.cluster();
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<FFGyms> call, Throwable t) {
                if (isDetached() || getActivity() == null) {
                    return;
                }
                GFMinimalNotification.make(getView(), t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
            }
        });
    }

    private void showList(final List<FFGym> gyms, final LatLng position, final boolean isFullscreen) {
        mAdapter = new GymListAdapter(gyms, this);
        mListView.setAdapter(mAdapter);
        adjustListContainerHeight(isFullscreen);
        if (!isListShown()) {
            // allow recycler view to pass initial drawing of children before animating up
            // this prevents an empty list showing after animation
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BottomSheetBehavior.from(mBottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                    BottomSheetBehavior.from(mBottomSheet).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int newState) {
                            if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                                BottomSheetBehavior.from(mBottomSheet).setBottomSheetCallback(null);
                            } else if (!isFullscreen) {
                                ensureSelectedMarkerVisible(position);
                            }

                            getActivity().supportInvalidateOptionsMenu();
                        }

                        @Override
                        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        }
                    });
                }
            }, 200);
        } else {
            BottomSheetBehavior.from(mBottomSheet).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    showList(gyms, position, isFullscreen);
                    BottomSheetBehavior.from(mBottomSheet).setBottomSheetCallback(null);
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
            BottomSheetBehavior.from(mBottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void hideList() {
        if (isListShown()) {
            BottomSheetBehavior.from(mBottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    private boolean isListShown() {
        return BottomSheetBehavior.from(mBottomSheet).getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    private void ensureSelectedMarkerVisible(LatLng position) {
        final Point markerPosition = getMap().getProjection().toScreenLocation(position);
        final int listTopInMap = mBottomSheet.getTop();
        final int markerPadding = getResources().getDimensionPixelSize(R.dimen.map_marker_padding);
        final int approximateMarkerBottomPosition = markerPosition.y + markerPadding;
        if (approximateMarkerBottomPosition > listTopInMap) {
            getMap().setOnCameraChangeListener(null);
            getMap().animateCamera(CameraUpdateFactory.scrollBy(0, markerPosition.y - listTopInMap + markerPadding), 500, null);
        }
    }

    private GoogleMap getMap() {
        return mMap;
    }

    private void adjustListContainerHeight(boolean isFullscreen) {
        if (isFullscreen) {
            ViewGroup.LayoutParams params = mBottomSheet.getLayoutParams();
            if (mAdapter.getItemCount() * getResources().getDimensionPixelSize(R.dimen.gym_list_item_height) < mMapView.getMeasuredHeight()) {
                mListViewTopShadow.setVisibility(View.VISIBLE);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                mListViewTopShadow.setVisibility(View.GONE);
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            mBottomSheet.setLayoutParams(params);
            return;
        }

        mListViewTopShadow.setVisibility(View.VISIBLE);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final TypedArray a = getActivity().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarHeight = a.getDimensionPixelSize(0, 0);
        a.recycle();
        int approximateMapHeight = size.y - 2 * actionBarHeight;
        int maxHeight = (int) Math.round(0.6 * approximateMapHeight);
        ViewGroup.LayoutParams params = mBottomSheet.getLayoutParams();
        if (mAdapter.getItemCount() == 0) {
            params.height = maxHeight / 2;
            mEmptyView.getLayoutParams().height = maxHeight / 2;
        } else if (mAdapter.getItemCount() * getResources().getDimensionPixelSize(R.dimen.gym_list_item_height) > maxHeight) {
            params.height = maxHeight;
        } else {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        mBottomSheet.setLayoutParams(params);
    }
}