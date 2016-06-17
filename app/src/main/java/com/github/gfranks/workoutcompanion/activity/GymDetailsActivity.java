package com.github.gfranks.workoutcompanion.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.view.Menu;
import android.view.View;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.base.BaseActivity;
import com.github.gfranks.workoutcompanion.adapter.DiscoverMapRenderer;
import com.github.gfranks.workoutcompanion.adapter.UserListAdapter;
import com.github.gfranks.workoutcompanion.adapter.holder.UserViewHolder;
import com.github.gfranks.workoutcompanion.data.api.DiscoverService;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.data.model.WCGyms;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.fragment.GymPhotosFragment;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.notification.WCInAppMessageManagerConstants;
import com.github.gfranks.workoutcompanion.util.AnimationUtils;
import com.github.gfranks.workoutcompanion.util.EndSheetBehavior;
import com.github.gfranks.workoutcompanion.util.GymDatabase;
import com.github.gfranks.workoutcompanion.view.EmptyView;
import com.github.gfranks.workoutcompanion.view.GymDetailsView;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;
import com.urbanairship.UAirship;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GymDetailsActivity extends BaseActivity implements Callback<WCGyms>, WCRecyclerView.OnItemClickListener,
        OnMapReadyCallback {

    private static final String FAVORITE = "favorite";

    @Inject
    DiscoverService mDiscoverService;
    @Inject
    WorkoutCompanionService mService;
    @Inject
    AccountManager mAccountManager;
    @Inject
    Picasso mPicasso;

    @InjectView(R.id.fab)
    FloatingActionButton mFab;
    @InjectView(R.id.gym_name)
    GymDetailsView mName;
    @InjectView(R.id.gym_address)
    GymDetailsView mAddress;
    @InjectView(R.id.mapview)
    MapView mMapView;
    @InjectView(R.id.gym_website)
    GymDetailsView mWebsite;
    @InjectView(R.id.gym_hours)
    GymDetailsView mHours;
    @InjectView(R.id.user_list)
    WCRecyclerView mListView;
    @InjectView(R.id.list_empty_text)
    EmptyView mEmptyView;

    private WCGym mGym;
    private UserListAdapter mAdapter;
    private GymDatabase mGymDatabase;
    private GoogleMap mMap;
    private ClusterManager<WCGym> mClusterManager;

    private boolean mIsFavorite, mTransitioned;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_details);
        setupTransitionListener();

        mGym = getIntent().getParcelableExtra(WCGym.EXTRA);
        mAdapter = new UserListAdapter();
        mEmptyView.setSubtitle(R.string.empty_users);
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);

        mGymDatabase = new GymDatabase(this);
        if (savedInstanceState != null) {
            mIsFavorite = savedInstanceState.getBoolean(FAVORITE, false);
        }

        mMapView.onCreate(null);
        initGym();
        mDiscoverService.getGymDetails(mGym.getPlace_id(), getString(R.string.api_places_key)).enqueue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        if (getMap() == null) {
            mMapView.getMapAsync(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        mGymDatabase.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FAVORITE, mIsFavorite);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_gym_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mTransitioned) {
            showFab();
        } else {
            hideFab();
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onStateChanged(@NonNull View endSheet, @EndSheetBehavior.State int newState) {
        if (newState == EndSheetBehavior.STATE_COLLAPSED) {
            showFab();
        } else {
            hideFab();
        }
    }

    @Override
    protected boolean popBackStackEntryOnBackPress() {
        hideFab();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                supportFinishAfterTransition();
            }
        }, AnimationUtils.DEFAULT_FAB_ANIM_DURATION);
        return false;
    }

    /**
     * **************************
     * Callback<WCDiscoverResult>
     * **************************
     */
    @Override
    public void onResponse(Call<WCGyms> call, Response<WCGyms> response) {
        if (isFinishing()) {
            return;
        }

        mGym = response.body().getResult();
        initGym();
    }

    @Override
    public void onFailure(Call<WCGyms> call, Throwable t) {
        if (isFinishing()) {
            return;
        }
        UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getErrorBuilder()
                .setAlert(t.getMessage())
                .create());
    }

    /**
     * **********************************
     * WCRecyclerView.OnItemClickListener
     * **********************************
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, int position) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(WCUser.EXTRA, mAdapter.getItem(position));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    ((UserViewHolder) vh).getImageViewForTransition(), getString(R.string.transition_user_image));
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    /**
     * ********************
     * View.OnClickListener
     * ********************
     */
    @OnClick(R.id.fab)
    void onFavorite() {
        if (mIsFavorite) {
            mIsFavorite = false;
            mFab.setImageResource(R.drawable.ic_unheart);
            mGymDatabase.deleteGym(mAccountManager.getUser().getId(), mGym.getId());
            UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getSuccessBuilder()
                    .setAlert(getString(R.string.gym_unfavorited))
                    .create());
        } else {
            mIsFavorite = true;
            mFab.setImageResource(R.drawable.ic_heart);
            mGymDatabase.saveGym(mAccountManager.getUser().getId(), mGym);
            UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getSuccessBuilder()
                    .setAlert(getString(R.string.gym_favorited))
                    .create());
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
            mClusterManager = new ClusterManager<>(this, getMap());
            mClusterManager.setRenderer(new DiscoverMapRenderer(this, getMap(), mClusterManager));
        }
        mClusterManager.clearItems();
        mClusterManager.addItem(mGym);
        getMap().getUiSettings().setAllGesturesEnabled(false);
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(mGym.getPosition(), 15));
    }

    private GoogleMap getMap() {
        return mMap;
    }

    private void initGym() {
        setTitle(mGym.getName());
        mName.setDescription(mGym.getName());
        if (mGym.getFormatted_address() != null && mGym.getFormatted_address().length() > 0) {
            mAddress.setDescription(mGym.getFormatted_address());
        } else {
            mAddress.setDescription(mGym.getVicinity());
        }

        if (mGym.getWebsite() != null && mGym.getWebsite().length() > 0) {
            mWebsite.setVisibility(View.VISIBLE);
            mWebsite.setDescription(mGym.getWebsite());
            mWebsite.setOnDescriptionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mGym.getWebsite()));
                    startActivity(intent);
                }
            });
        }

        if (mGym.getOpening_hours() != null && mGym.getOpening_hours().getWeekday_text() != null
                && !mGym.getOpening_hours().getWeekday_text().isEmpty()) {
            mHours.setVisibility(View.VISIBLE);
            mHours.setDescription(mGym.getOpening_hours().toString());
        }

        ((GymPhotosFragment) getSupportFragmentManager().findFragmentById(R.id.images_fragment)).setGym(mGym);

        mService.getUsers(mGym.getPlace_id()).enqueue(new Callback<List<WCUser>>() {
            @Override
            public void onResponse(Call<List<WCUser>> call, Response<List<WCUser>> response) {
                if (isFinishing()) {
                    return;
                }

                mAdapter.setUsers(response.body());
            }

            @Override
            public void onFailure(Call<List<WCUser>> call, Throwable t) {
                if (isFinishing()) {
                    return;
                }
                UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getErrorBuilder()
                        .setAlert(t.getMessage())
                        .create());
            }
        });

        try {
            mGymDatabase.open();
            mIsFavorite = mGymDatabase.isFavorite(mAccountManager.getUser().getId(), mGym.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            // unable to open gym db
        }

        if (mIsFavorite) {
            mFab.setImageResource(R.drawable.ic_heart);
        } else {
            mFab.setImageResource(R.drawable.ic_unheart);
        }
    }

    private void setupTransitionListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().addListener(new AnimationUtils.DefaultTransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    mTransitioned = true;
                    supportInvalidateOptionsMenu();
                }
            });
        } else {
            mTransitioned = true;
            supportInvalidateOptionsMenu();
        }
    }

    private void showFab() {
        if (mFab.getScaleX() == 1 && mFab.getScaleY() == 1) {
            return;
        }
        AnimatorSet set = new AnimatorSet();
        set.setDuration(AnimationUtils.DEFAULT_FAB_ANIM_DURATION);
        set.playTogether(ObjectAnimator.ofFloat(mFab, "scaleX", 0, 1), ObjectAnimator.ofFloat(mFab, "scaleY", 0, 1));
        set.start();
        mFab.setVisibility(View.VISIBLE);
    }

    private void hideFab() {
        if (mFab.getScaleX() == 0 && mFab.getScaleY() == 0) {
            // handle case where user rotates to ensure we remove the fab
            mFab.setVisibility(View.GONE);
            return;
        }
        AnimatorSet set = new AnimatorSet();
        set.setDuration(AnimationUtils.DEFAULT_FAB_ANIM_DURATION);
        set.playTogether(ObjectAnimator.ofFloat(mFab, "scaleX", 1, 0), ObjectAnimator.ofFloat(mFab, "scaleY", 1, 0));
        set.addListener(new AnimationUtils.DefaultAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFab.setVisibility(View.GONE);
            }
        });
        set.start();
    }
}