package com.github.gfranks.workoutcompanion.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.UserProfileActivity;
import com.github.gfranks.workoutcompanion.adapter.DiscoverMapRenderer;
import com.github.gfranks.workoutcompanion.adapter.SearchSuggestionsAdapter;
import com.github.gfranks.workoutcompanion.adapter.UserListAdapter;
import com.github.gfranks.workoutcompanion.adapter.holder.UserViewHolder;
import com.github.gfranks.workoutcompanion.data.api.DiscoverService;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.model.WCDiscoverResponse;
import com.github.gfranks.workoutcompanion.data.model.WCDiscoverResult;
import com.github.gfranks.workoutcompanion.data.model.WCLocationResponse;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.manager.DiscoverManager;
import com.github.gfranks.workoutcompanion.notification.WCInAppMessageManagerConstants;
import com.github.gfranks.workoutcompanion.view.EmptyView;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;
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
import com.urbanairship.UAirship;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverMapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ClusterManager.OnClusterClickListener<WCDiscoverResult>, ClusterManager.OnClusterItemClickListener<WCDiscoverResult>,
        GoogleMap.OnMapClickListener, WCRecyclerView.OnItemClickListener, SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener {

    public static final String TAG = "discover_map_fragment";

    @Inject
    DiscoverManager mDiscoverManager;
    @Inject
    DiscoverService mDiscoverService;
    @Inject
    WorkoutCompanionService mService;

    @InjectView(R.id.map_view)
    MapView mMapView;
    @InjectView(R.id.map_view_list_container)
    View mBottomSheet;
    @InjectView(R.id.map_view_list)
    WCRecyclerView mListView;
    @InjectView(R.id.list_empty_text)
    EmptyView mEmptyView;

    private GoogleMap mMap;
    private ClusterManager<WCDiscoverResult> mClusterManager;
    private UserListAdapter mAdapter;
    private SearchView mSearchView;
    private SearchSuggestionsAdapter mSearchViewAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new UserListAdapter();
        mSearchViewAdapter = new SearchSuggestionsAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.onCreate(savedInstanceState);
        mEmptyView.setSubtitle(R.string.empty_gym);
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        if (getMap() == null) {
            mMapView.getMapAsync(this);
        } else if (!mDiscoverManager.isConnected()) {
            mDiscoverManager.connect(this, this, this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDiscoverManager.disconnect();
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == DiscoverManager.REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getMap().setMyLocationEnabled(true);
            mDiscoverManager.connect(this, this, this);
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
        mClusterManager = new ClusterManager<>(getContext(), getMap());
        mClusterManager.setRenderer(new DiscoverMapRenderer(getContext(), getMap(), mClusterManager));
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnMapClickListener(this);
        if (mDiscoverManager.connect(this, this, this)) {
            map.setMyLocationEnabled(true);
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
    public boolean onClusterClick(final Cluster<WCDiscoverResult> cluster) {
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
    }

    /**
     * *****************************************
     * ClusterManager.OnClusterItemClickListener
     * *****************************************
     */
    @Override
    public boolean onClusterItemClick(WCDiscoverResult wcDiscoverResult) {
        final LatLng position = wcDiscoverResult.getPosition();
        mService.getUsers(wcDiscoverResult.getId()).enqueue(new Callback<List<WCUser>>() {
            @Override
            public void onResponse(Call<List<WCUser>> call, Response<List<WCUser>> response) {
                if (isDetached() || getActivity() == null) {
                    return;
                }
                mAdapter.setUsers(response.body());
                showList(position);
            }

            @Override
            public void onFailure(Call<List<WCUser>> call, Throwable t) {
                if (isDetached() || getActivity() == null) {
                    return;
                }
                UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getErrorBuilder()
                        .setAlert(t.getMessage())
                        .create());
            }
        });
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
        UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getErrorBuilder()
                .setAlert(getString(R.string.error_no_location))
                .create());
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
            mDiscoverService.getLocations(address, getString(R.string.api_places_key)).enqueue(new Callback<WCLocationResponse>() {
                @Override
                public void onResponse(Call<WCLocationResponse> call, Response<WCLocationResponse> response) {
                    if (isDetached() || getActivity() == null) {
                        return;
                    }
                    mSearchViewAdapter.updateWithLocationResults(response.body().getResults());
                }

                @Override
                public void onFailure(Call<WCLocationResponse> call, Throwable t) {
                    if (isDetached() || getActivity() == null) {
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
        mDiscoverManager.setLastKnownLocation(mSearchViewAdapter.getResultItem(position).getPosition());
        getActivity().supportInvalidateOptionsMenu();
        loadGyms();
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    private void loadGyms() {
        LatLng latLng = mDiscoverManager.getLastKnownLocation();
        mDiscoverService.getGyms(latLng.latitude + "," + latLng.longitude, getString(R.string.api_places_key)).enqueue(new Callback<WCDiscoverResponse>() {
            @Override
            public void onResponse(Call<WCDiscoverResponse> call, Response<WCDiscoverResponse> response) {
                if (isDetached() || getActivity() == null) {
                    return;
                }
                mClusterManager.clearItems();
                mClusterManager.addItems(response.body().getResults());
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (ClusterItem item : response.body().getResults()) {
                    builder.include(item.getPosition());
                }
                final LatLngBounds bounds = builder.build();
                getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }

            @Override
            public void onFailure(Call<WCDiscoverResponse> call, Throwable t) {
                if (isDetached() || getActivity() == null) {
                    return;
                }
                UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getErrorBuilder()
                        .setAlert(t.getMessage())
                        .create());
            }
        });
    }

    private void showList(final LatLng position) {
        adjustListContainerHeight();
        if (BottomSheetBehavior.from(mBottomSheet).getState() != BottomSheetBehavior.STATE_EXPANDED) {
            // allow recycler view to pass initial drawing of children before animating up
            // this prevents an empty list showing after animation
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BottomSheetBehavior.from(mBottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                    BottomSheetBehavior.from(mBottomSheet).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int newState) {
                            ensureSelectedMarkerVisible(position);
                            BottomSheetBehavior.from(mBottomSheet).setBottomSheetCallback(null);
                        }

                        @Override
                        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        }
                    });
                }
            }, 200);
        } else {
            ensureSelectedMarkerVisible(position);
        }
    }

    private void hideList() {
        if (BottomSheetBehavior.from(mBottomSheet).getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            BottomSheetBehavior.from(mBottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void ensureSelectedMarkerVisible(LatLng position) {
        final Point markerPosition = getMap().getProjection().toScreenLocation(position);
        final int listTopInMap = mBottomSheet.getTop() + mListView.getTop();
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

    private void adjustListContainerHeight() {
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
        } else if (mAdapter.getItemCount() * getResources().getDimensionPixelSize(R.dimen.user_list_item_height) > maxHeight) {
            params.height = maxHeight;
        } else {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        mBottomSheet.setLayoutParams(params);
    }
}