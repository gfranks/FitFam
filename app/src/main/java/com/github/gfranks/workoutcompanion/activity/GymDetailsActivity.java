package com.github.gfranks.workoutcompanion.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.base.BaseActivity;
import com.github.gfranks.workoutcompanion.adapter.DiscoverMapRenderer;
import com.github.gfranks.workoutcompanion.adapter.UserListAdapter;
import com.github.gfranks.workoutcompanion.adapter.holder.UserViewHolder;
import com.github.gfranks.workoutcompanion.data.api.GoogleApiService;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.data.model.WCGyms;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.fragment.GymPhotosFragment;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.manager.FilterManager;
import com.github.gfranks.workoutcompanion.util.AnimationUtils;
import com.github.gfranks.workoutcompanion.util.GymDatabase;
import com.github.gfranks.workoutcompanion.util.GymUtils;
import com.github.gfranks.workoutcompanion.util.Utils;
import com.github.gfranks.workoutcompanion.view.EmptyView;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GymDetailsActivity extends BaseActivity implements Callback<WCGyms>, NestedScrollView.OnScrollChangeListener,
        WCRecyclerView.OnItemClickListener, OnMapReadyCallback, CompoundButton.OnCheckedChangeListener {

    private static final int REQUEST_CODE_PHONE_CALL = 1;

    @Inject
    GoogleApiService mGoogleApiService;
    @Inject
    WorkoutCompanionService mService;
    @Inject
    AccountManager mAccountManager;
    @Inject
    FilterManager mFilterManager;

    @InjectView(R.id.gym_favorite)
    ToggleButton mFavorite;
    @InjectView(R.id.gym_set_as_home)
    View mSetAsHomeGym;
    @InjectView(R.id.scroll_view)
    NestedScrollView mScrollView;
    @InjectView(R.id.gym_name)
    TextView mName;
    @InjectView(R.id.gym_address)
    TextView mAddress;
    @InjectView(R.id.gym_action_buttons)
    ViewGroup mActionButtons;
    @InjectView(R.id.gym_share)
    ImageButton mShare;
    @InjectView(R.id.gym_add_remove)
    ImageButton mAddRemoveGym;
    @InjectView(R.id.gym_call)
    ImageButton mCall;
    @InjectView(R.id.gym_favorite_alt)
    ImageButton mFavoriteAlt;
    @InjectView(R.id.gym_website)
    TextView mWebsite;
    @InjectView(R.id.gym_reviews)
    TextView mReviews;
    @InjectView(R.id.gym_ratings_container)
    ViewGroup mRatingsContainer;
    @InjectView(R.id.gym_ratings_title)
    TextView mRatingsTitle;
    @InjectView(R.id.gym_hours_container)
    View mHoursContainer;
    @InjectView(R.id.gym_hours)
    TextView mHours;
    @InjectView(R.id.mapview)
    MapView mMapView;
    @InjectView(R.id.user_list)
    WCRecyclerView mListView;
    @InjectView(R.id.list_empty_text)
    EmptyView mEmptyView;

    private WCGym mGym;
    private UserListAdapter mAdapter;
    private GymDatabase mGymDatabase;
    private GoogleMap mMap;
    private ClusterManager<WCGym> mClusterManager;

    private boolean mTransitioned, mShowActionMenuItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_details);
        setupTransitionListener();

        mGym = getIntent().getParcelableExtra(WCGym.EXTRA);
        mGymDatabase = new GymDatabase(this);

        mAdapter = new UserListAdapter();
        mEmptyView.setSubtitle(R.string.empty_users);
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);

        mScrollView.setOnScrollChangeListener(this);
        mMapView.onCreate(null);
        initGym();
        mGoogleApiService.getGymDetails(mGym.getPlace_id(), getString(R.string.api_places_key)).enqueue(this);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makePhoneCall();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_gym_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_call).setVisible(mShowActionMenuItems);
        menu.findItem(R.id.action_share).setVisible(mShowActionMenuItems);
        menu.findItem(R.id.action_add_remove).setVisible(mShowActionMenuItems);
        menu.findItem(R.id.action_save).setVisible(mShowActionMenuItems);

        if (mGym.getFormatted_phone_number() != null && mGym.getFormatted_phone_number().length() > 0) {
            menu.findItem(R.id.action_call).setEnabled(true);
            mCall.setEnabled(true);
            mCall.setAlpha(1f);
        } else {
            menu.findItem(R.id.action_call).setEnabled(false);
            mCall.setEnabled(false);
            mCall.setAlpha(0.5f);
        }

        if (mAccountManager.getUser().getGymIds().contains(mGym.getPlace_id())) {
            menu.findItem(R.id.action_add_remove).setIcon(R.drawable.ic_remove);
            menu.findItem(R.id.action_add_remove).setTitle(R.string.action_remove);
            mAddRemoveGym.setImageResource(R.drawable.ic_remove);
        } else {
            menu.findItem(R.id.action_add_remove).setIcon(R.drawable.ic_add);
            menu.findItem(R.id.action_add_remove).setTitle(R.string.action_add);
            mAddRemoveGym.setImageResource(R.drawable.ic_add);
        }

        if (mFavorite.isChecked()) {
            menu.findItem(R.id.action_save).setIcon(R.drawable.ic_heart_on);
            mFavoriteAlt.setImageResource(R.drawable.ic_heart_on);
        } else {
            menu.findItem(R.id.action_save).setIcon(R.drawable.ic_heart_off);
            mFavoriteAlt.setImageResource(R.drawable.ic_heart_off);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_call:
                onClick(mCall);
                break;
            case R.id.action_share:
                onClick(mShare);
                break;
            case R.id.action_add_remove:
                onClick(mAddRemoveGym);
                break;
            case R.id.action_save:
                onClick(mFavoriteAlt);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout, verticalOffset);
        if (mAppBarCollapsed) {
            // ensure the set as home gym button stays above the toolbar on collapse
            ViewCompat.setElevation(mSetAsHomeGym, mAppBarLayout.getTargetElevation() + 1);
        } else {
            ViewCompat.setElevation(mSetAsHomeGym, mAppBarLayout.getTargetElevation());
        }

        if (Math.abs(verticalOffset) >= mFavorite.getBottom() && !mFavoriteAlt.isEnabled()) {
            mFavoriteAlt.setEnabled(true);
            mFavoriteAlt.animate().alpha(1f).start();
        } else if (Math.abs(verticalOffset) < mFavorite.getBottom() && mFavoriteAlt.isEnabled()) {
            mFavoriteAlt.setEnabled(false);
            mFavoriteAlt.animate().alpha(0.5f).start();
        }
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
        GFMinimalNotification.make(mCoordinatorLayout, t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
    }

    /**
     * ***************************************
     * NestedScrollView.OnScrollChangeListener
     * ***************************************
     */
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (scrollY >= mActionButtons.getTop() && !mShowActionMenuItems) {
            mShowActionMenuItems = true;
            supportInvalidateOptionsMenu();
        } else if (scrollY < mActionButtons.getTop() && mShowActionMenuItems) {
            mShowActionMenuItems = false;
            supportInvalidateOptionsMenu();
        }
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
    @OnClick({R.id.gym_set_as_home, R.id.gym_share, R.id.gym_add_remove, R.id.gym_call, R.id.gym_favorite_alt, R.id.gym_website, R.id.gym_ratings_container})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.gym_set_as_home: {
                WCUser user = mAccountManager.getUser();
                user.setHomeGym(mGym.getName());
                user.setHomeGymId(mGym.getPlace_id());
                mService.updateUser(user.getId(), user).enqueue(new Callback<WCUser>() {
                    @Override
                    public void onResponse(Call<WCUser> call, Response<WCUser> response) {
                        if (isFinishing()) {
                            return;
                        }
                        mAccountManager.setUser(response.body());
                        mSetAsHomeGym.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<WCUser> call, Throwable t) {
                        if (isFinishing()) {
                            return;
                        }
                        GFMinimalNotification.make(mCoordinatorLayout, t.getMessage(), GFMinimalNotification.LENGTH_LONG).show();
                    }
                });
                break;
            }
            case R.id.gym_share: {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.gym_share_text, mGym.getName(), mGym.getUrl())));
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.gym_share)));
                break;
            }
            case R.id.gym_add_remove: {
                WCUser user = mAccountManager.getUser();
                if (user.getGymIds().contains(mGym.getPlace_id())) {
                    user.getGymIds().remove(mGym.getPlace_id());
                } else {
                    user.getGymIds().add(mGym.getPlace_id());
                }
                mService.updateUser(user.getId(), user).enqueue(new Callback<WCUser>() {
                    @Override
                    public void onResponse(Call<WCUser> call, Response<WCUser> response) {
                        if (isFinishing()) {
                            return;
                        }
                        mAccountManager.setUser(response.body());
                        if (response.body().getGymIds().contains(mGym.getPlace_id())) {
                            GFMinimalNotification.make(mCoordinatorLayout, R.string.gym_added, GFMinimalNotification.LENGTH_LONG).show();
                        } else {
                            GFMinimalNotification.make(mCoordinatorLayout, R.string.gym_removed, GFMinimalNotification.LENGTH_LONG).show();
                        }
                        supportInvalidateOptionsMenu();
                    }

                    @Override
                    public void onFailure(Call<WCUser> call, Throwable t) {
                        if (isFinishing()) {
                            return;
                        }
                        GFMinimalNotification.make(mCoordinatorLayout, t.getMessage(), GFMinimalNotification.LENGTH_LONG).show();
                    }
                });
                break;
            }
            case R.id.gym_call: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CALL_PHONE)) {
                        GFMinimalNotification.make(mCoordinatorLayout, R.string.gym_call_permission_reason, GFMinimalNotification.LENGTH_LONG).show();
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                REQUEST_CODE_PHONE_CALL);
                    }
                } else {
                    makePhoneCall();
                }
                break;
            }
            case R.id.gym_favorite_alt: {
                mFavorite.toggle();
                break;
            }
            case R.id.gym_website: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mGym.getWebsite()));
                startActivity(intent);
            }
            case R.id.gym_ratings_container: {
                Intent intent = new Intent(this, GymReviewsActivity.class);
                intent.putExtra(WCGym.EXTRA, mGym);
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * **************************************
     * CompoundButton.OnCheckedChangeListener
     * **************************************
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mFavoriteAlt.setImageResource(R.drawable.ic_heart_on);
            mGymDatabase.saveGym(mAccountManager.getUser().getId(), mGym);
            GFMinimalNotification.make(mCoordinatorLayout, R.string.gym_favorited, GFMinimalNotification.LENGTH_LONG).show();
        } else {
            mFavoriteAlt.setImageResource(R.drawable.ic_heart_off);
            mGymDatabase.deleteGym(mAccountManager.getUser().getId(), mGym.getId());
            GFMinimalNotification.make(mCoordinatorLayout, R.string.gym_unfavorited, GFMinimalNotification.LENGTH_LONG).show();
        }
        supportInvalidateOptionsMenu();
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

        if (mGym.getPlace_id().equals(mAccountManager.getUser().getHomeGymId())) {
            mSetAsHomeGym.setVisibility(View.GONE);
        }

        mName.setText(mGym.getName());
        if (mGym.getFormatted_address() != null && mGym.getFormatted_address().length() > 0) {
            mAddress.setText(mGym.getFormatted_address());
        } else {
            mAddress.setText(mGym.getVicinity());
        }

        if (mGym.getWebsite() != null && mGym.getWebsite().length() > 0) {
            mWebsite.setVisibility(View.VISIBLE);
            mWebsite.setText(mGym.getWebsite());
        } else {
            mWebsite.setVisibility(View.GONE);
        }

        if (mGym.getReviews() != null && mGym.getReviews().size() > 0) {
            mRatingsContainer.setVisibility(View.VISIBLE);
            mReviews.setText(mGym.getReviews().size() + " " + getString(R.string.gym_reviews));
            if (mGym.getRating() > 0f) {
                mRatingsTitle.setText(getString(R.string.gym_rating) + ": " + String.valueOf(mGym.getRating()));
                ImageView[] ivs = new ImageView[]{
                        (ImageView) ((ViewGroup) mRatingsContainer.getChildAt(2)).getChildAt(0),
                        (ImageView) ((ViewGroup) mRatingsContainer.getChildAt(2)).getChildAt(1),
                        (ImageView) ((ViewGroup) mRatingsContainer.getChildAt(2)).getChildAt(2),
                        (ImageView) ((ViewGroup) mRatingsContainer.getChildAt(2)).getChildAt(3),
                        (ImageView) ((ViewGroup) mRatingsContainer.getChildAt(2)).getChildAt(4)
                };
                GymUtils.adjustImageViewsForRating(this, mGym.getRating(), ivs);
            } else {
                mRatingsTitle.setText(R.string.gym_rating_unavailable);
                Drawable drawable = Utils.applyDrawableTint(this, R.drawable.ic_star, ContextCompat.getColor(this, R.color.gray_lightest));
                ((ImageView) ((ViewGroup) mRatingsContainer.getChildAt(2)).getChildAt(0)).setImageDrawable(drawable);
                ((ImageView) ((ViewGroup) mRatingsContainer.getChildAt(2)).getChildAt(1)).setImageDrawable(drawable);
                ((ImageView) ((ViewGroup) mRatingsContainer.getChildAt(2)).getChildAt(2)).setImageDrawable(drawable);
                ((ImageView) ((ViewGroup) mRatingsContainer.getChildAt(2)).getChildAt(3)).setImageDrawable(drawable);
                ((ImageView) ((ViewGroup) mRatingsContainer.getChildAt(2)).getChildAt(4)).setImageDrawable(drawable);
            }
        } else {
            mRatingsContainer.setVisibility(View.GONE);
        }

        if (mGym.getOpening_hours() != null && mGym.getOpening_hours().getWeekday_text() != null
                && !mGym.getOpening_hours().getWeekday_text().isEmpty()) {
            mHoursContainer.setVisibility(View.VISIBLE);
            mHours.setText(mGym.getOpening_hours().toString());
        } else {
            mHoursContainer.setVisibility(View.GONE);
        }

        ((GymPhotosFragment) getSupportFragmentManager().findFragmentById(R.id.images_fragment)).setGym(mGym);

        mService.getUsers(mGym.getPlace_id(), mFilterManager.getFilterOptions()).enqueue(new Callback<List<WCUser>>() {
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
                GFMinimalNotification.make(mCoordinatorLayout, t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
            }
        });

        try {
            if (!mGymDatabase.isOpen()) {
                mGymDatabase.open();
            }
            mFavorite.setOnCheckedChangeListener(null);
            boolean isFavorite = mGymDatabase.isFavorite(mAccountManager.getUser().getId(), mGym.getId());
            mFavorite.setChecked(isFavorite);
            mFavorite.setOnCheckedChangeListener(this);
        } catch (Throwable t) {
            t.printStackTrace();
            // unable to open gym db
        }
        supportInvalidateOptionsMenu();
    }

    private void makePhoneCall() {
        new MaterialDialog.Builder(this)
                .content(getString(R.string.gym_call, mGym.getName(), mGym.getFormatted_phone_number()))
                .positiveText(R.string.action_call)
                .negativeText(R.string.action_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (ContextCompat.checkSelfPermission(GymDetailsActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            String uri = "tel:" + mGym.getFormatted_phone_number();
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(uri));
                            startActivity(intent);
                        }
                    }
                });
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
}