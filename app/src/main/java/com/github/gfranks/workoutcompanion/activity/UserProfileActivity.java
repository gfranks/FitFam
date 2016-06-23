package com.github.gfranks.workoutcompanion.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.base.BaseActivity;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.dialog.SelectGymDialog;
import com.github.gfranks.workoutcompanion.fragment.ExerciseTypeFragment;
import com.github.gfranks.workoutcompanion.fragment.WeightSelectFragment;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.util.AnimationUtils;
import com.github.gfranks.workoutcompanion.util.CropCircleTransformation;
import com.github.gfranks.workoutcompanion.util.EndSheetBehavior;
import com.github.gfranks.workoutcompanion.util.Utils;
import com.github.gfranks.workoutcompanion.util.ValidatorUtils;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends BaseActivity implements Callback<WCUser> {

    private static final String CAN_EDIT = "can_edit";
    private static final String EDIT_MODE = "edit_mode";

    @Inject
    WorkoutCompanionService mService;
    @Inject
    AccountManager mAccountManager;
    @Inject
    Picasso mPicasso;

    @InjectView(R.id.image)
    ImageView mImage;
    @InjectView(R.id.set_public)
    CheckBox mPublic;
    @InjectView(R.id.fab)
    FloatingActionButton mFab;
    @InjectView(R.id.first_name)
    MaterialEditText mFirstName;
    @InjectView(R.id.last_name)
    MaterialEditText mLastName;
    @InjectView(R.id.email)
    MaterialEditText mEmail;
    @InjectView(R.id.phone_number)
    MaterialEditText mPhoneNumber;
    @InjectView(R.id.male)
    RadioButton mMale;
    @InjectView(R.id.female)
    RadioButton mFemale;
    @InjectView(R.id.birthday)
    MaterialEditText mBirthday;
    @InjectView(R.id.birthday_select)
    AppCompatImageView mBirthdaySelect;
    @InjectView(R.id.home_gym)
    Button mHomeGym;

    private WCUser mUser;
    private boolean mIsNewUser, mCanEdit, mEditMode, mTransitioned, mExiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupTransitionListener();

        if (savedInstanceState != null) {
            mUser = savedInstanceState.getParcelable(WCUser.EXTRA);
            mEditMode = savedInstanceState.getBoolean(EDIT_MODE);
            mCanEdit = savedInstanceState.getBoolean(CAN_EDIT);
            mTransitioned = true;
        } else {
            if (getIntent().hasExtra(WCUser.EXTRA)) {
                mUser = getIntent().getParcelableExtra(WCUser.EXTRA);
                mCanEdit = mUser.equals(mAccountManager.getUser());
            } else {
                mUser = mAccountManager.getUser();
                mCanEdit = true;
            }

            mService.getUser(mUser.getId()).enqueue(this);
        }

        if (getIntent().hasExtra(WCUser.EXTRA_NEW)) {
            mIsNewUser = true;
            mEditMode = true;
        }

        mFirstName.addValidator(ValidatorUtils.getNonEmptyValidator(getString(R.string.error_no_first_name)));
        mLastName.addValidator(ValidatorUtils.getNonEmptyValidator(getString(R.string.error_no_last_name)));
        mEmail.addValidator(ValidatorUtils.getEmailValidator(this));
        mPhoneNumber.addValidator(ValidatorUtils.getPhoneNumberValidator(this));

        if (!mCanEdit) {
            mBirthdaySelect.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mIsNewUser) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInfo();
        outState.putParcelable(WCUser.EXTRA, mUser);
        outState.putBoolean(EDIT_MODE, mEditMode);
        outState.putBoolean(CAN_EDIT, mCanEdit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_save).setVisible(mEditMode && mCanEdit);
        if (mIsNewUser) {
            menu.findItem(R.id.action_cancel).setVisible(false);
            menu.findItem(R.id.action_call).setVisible(false);
            menu.findItem(R.id.action_message).setVisible(false);
            menu.findItem(R.id.action_request_workout).setVisible(false);
        } else {
            menu.findItem(R.id.action_cancel).setVisible(mEditMode && mCanEdit);
            menu.findItem(R.id.action_call).setVisible(!mUser.equals(mAccountManager.getUser())
                    && Utils.isPhoneNumber(mUser.getPhoneNumber()) && (mUser.isCanSeeContactInfo() || mUser.isPublic()));
            menu.findItem(R.id.action_message).setVisible(!mUser.equals(mAccountManager.getUser())
                    && Utils.isPhoneNumber(mUser.getPhoneNumber()) && (mUser.isCanSeeContactInfo() || mUser.isPublic()));
            menu.findItem(R.id.action_request_workout).setVisible(!mCanEdit && !mUser.isCanSeeContactInfo() && !mUser.isPublic());
        }

        if (mCanEdit && !mEditMode && !mIsNewUser && mTransitioned && !mExiting) {
            showFab();
        } else {
            hideFab();
        }

        mImage.setEnabled(mEditMode);
        mPublic.setEnabled(mEditMode);
        if (mPublic.isEnabled()) {
            mPublic.setText(R.string.set_public);
        } else {
            if (mPublic.isChecked()) {
                mPublic.setText(R.string.is_public);
            } else {
                mPublic.setText(R.string.is_private);
            }
        }
        mFirstName.setEnabled(mEditMode);
        mLastName.setEnabled(mEditMode);
        mEmail.setEnabled(mEditMode);
        mPhoneNumber.setEnabled(mEditMode);
        mMale.setEnabled(mEditMode);
        mFemale.setEnabled(mEditMode);
        mBirthday.setEnabled(mEditMode);
        mBirthdaySelect.setEnabled(mEditMode);
        mHomeGym.setEnabled(mEditMode);
        if (mCanEdit) {
            if (mEditMode) {
                if (mUser.getHomeGym() == null || mUser.getHomeGym().length() == 0) {
                    mHomeGym.setText(R.string.home_gym_select);
                }
                mBirthdaySelect.setColorFilter(ContextCompat.getColor(this, R.color.theme_icon_color));
            } else {
                if (mUser.getHomeGym() == null || mUser.getHomeGym().length() == 0) {
                    mHomeGym.setText(R.string.home_gym_not_selected);
                }
                mBirthdaySelect.setColorFilter(ContextCompat.getColor(this, R.color.theme_icon_color_light));
            }
        }

        ((WeightSelectFragment) getSupportFragmentManager().findFragmentById(R.id.weight_select_fragment)).edit(mEditMode);
        ((ExerciseTypeFragment) getSupportFragmentManager().findFragmentById(R.id.exercise_type_fragment)).edit(mEditMode);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            save();
        } else if (id == R.id.action_cancel) {
            // reset data
            initUser();
            mEditMode = false;
            supportInvalidateOptionsMenu();
        } else if (id == R.id.action_call) {
            // call
        } else if (id == R.id.action_message) {
            // message
        } else if (id == R.id.action_request_workout) {
            requestWorkout();
        }

        return super.onOptionsItemSelected(item);
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
        continueOn();
        return false;
    }

    /**
     * ********************
     * View.OnClickListener
     * ********************
     */
    @OnClick({R.id.image, R.id.fab, R.id.birthday_select, R.id.home_gym})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.image:
                // edit image
                break;
            case R.id.fab:
                mEditMode = true;
                supportInvalidateOptionsMenu();
                break;
            case R.id.birthday_select:
                Calendar c = Calendar.getInstance();
                int month, day, year;
                if (mUser.getBirthday() != null && mUser.getBirthday().length() > 0) {
                    month = mUser.getBirthdayMonth() - 1;
                    day = mUser.getBirthdayDayOfMonth();
                    year = mUser.getBirthdayYear();
                } else {
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                    year = c.get(Calendar.YEAR);
                }
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        StringBuilder birthday = new StringBuilder();
                        if (++monthOfYear < 10) {
                            birthday.append("0");
                        }
                        birthday.append(monthOfYear);
                        birthday.append("/");
                        if (dayOfMonth < 10) {
                            birthday.append("0");
                        }
                        birthday.append(dayOfMonth);
                        birthday.append("/");
                        birthday.append(year);
                        mBirthday.setText(birthday.toString());
                        mUser.setBirthday(birthday.toString());
                    }
                }, year, month, day).show();
                break;
            case R.id.home_gym:
                SelectGymDialog.newInstance(this, false, new SelectGymDialog.OnGymSelectedListener() {
                    @Override
                    public void onGymSelected(SelectGymDialog dialog, WCGym gym) {
                        mUser.setHomeGymId(gym.getPlace_id());
                        mUser.setHomeGym(gym.getName());
                        mHomeGym.setText(gym.getName());
                    }
                }).show();
                break;
        }
    }

    /**
     * ****************
     * Callback<WCUser>
     * ****************
     */
    @Override
    public void onResponse(Call<WCUser> call, Response<WCUser> response) {
        if (isFinishing()) {
            return;
        }
        mUser = response.body();
        initUser();
    }

    @Override
    public void onFailure(Call<WCUser> call, Throwable t) {
        if (isFinishing()) {
            return;
        }
        GFMinimalNotification.make(mCoordinatorLayout, t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();

        supportFinishAfterTransition();
    }

    private void initUser() {
        if (!mCanEdit) {
            setTitle(mUser.getFullName());
        }
        Drawable defaultImage = ContextCompat.getDrawable(this, R.drawable.ic_avatar);
        if (mUser.getImage() != null && !mUser.getImage().isEmpty()) {
            mPicasso.load(mUser.getImage())
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .transform(new CropCircleTransformation())
                    .into(mImage);
        } else {
            mImage.setImageDrawable(defaultImage);
        }

        mPublic.setChecked(mUser.isPublic());
        mFirstName.setText(mUser.getFirstName());
        mLastName.setText(mUser.getLastName());
        if (mUser.getHomeGym() != null && mUser.getHomeGym().length() > 0) {
            mHomeGym.setText(mUser.getHomeGym());
        } else if (!mCanEdit) {
            mHomeGym.setText(R.string.home_gym_not_selected);
        }
        if (!mCanEdit && !mUser.isCanSeeContactInfo() && !mUser.isPublic()) {
            mEmail.setText(R.string.email_blank);
            mPhoneNumber.setText(R.string.phone_number_blank);
            mBirthday.setText(R.string.birthday_blank);
            mEmail.setTypeface(null, Typeface.ITALIC);
            mPhoneNumber.setTypeface(null, Typeface.ITALIC);
            mBirthday.setTypeface(null, Typeface.ITALIC);
        } else {
            mEmail.setText(mUser.getEmail());
            mPhoneNumber.setText(mUser.getPhoneNumber());
            mBirthday.setText(mUser.getBirthday());
        }
        if (mUser.isFemale()) {
            mFemale.setChecked(true);
        } else {
            mMale.setChecked(true);
        }

        WeightSelectFragment weightSelectFragment = (WeightSelectFragment) getSupportFragmentManager().findFragmentById(R.id.weight_select_fragment);
        weightSelectFragment.setWeight(mUser.getWeight());

        ExerciseTypeFragment exerciseTypeFragment = (ExerciseTypeFragment) getSupportFragmentManager().findFragmentById(R.id.exercise_type_fragment);
        exerciseTypeFragment.setUser(mUser);
    }

    private void saveInfo() {
        mUser.setPublic(mPublic.isChecked());
        mUser.setFirstName(mFirstName.getText().toString());
        mUser.setLastName(mLastName.getText().toString());
        mUser.setEmail(mEmail.getText().toString());
        mUser.setPhoneNumber(mPhoneNumber.getText().toString());
        mUser.setBirthday(mBirthday.getText().toString());
        mUser.setSex(mMale.isChecked() ? WCUser.MALE : WCUser.FEMALE);
        mUser.setWeight(((WeightSelectFragment) getSupportFragmentManager().findFragmentById(R.id.weight_select_fragment)).getWeight());
        mUser.setExercises(((ExerciseTypeFragment) getSupportFragmentManager().findFragmentById(R.id.exercise_type_fragment)).getExercises());
    }

    private void save() {
        mPhoneNumber.setText(Utils.removeNonDigitValuesFromPhoneNumber(mPhoneNumber.getText().toString(), true));
        if (mFirstName.validate() && mLastName.validate() && mEmail.validate() && mPhoneNumber.validate()
                && mBirthday.validate() && (mMale.isChecked() || mFemale.isChecked())) {
            mEditMode = false;
            mExiting = true;
            supportInvalidateOptionsMenu();

            saveInfo();
            mService.updateUser(mUser.getId(), mUser).enqueue(new Callback<WCUser>() {
                @Override
                public void onResponse(Call<WCUser> call, Response<WCUser> response) {
                    if (isFinishing()) {
                        return;
                    }
                    if (mAccountManager.getUser().equals(response.body())) {
                        mAccountManager.setUser(response.body());
                    }
                    continueOn();
                }

                @Override
                public void onFailure(Call<WCUser> call, Throwable t) {
                    if (isFinishing()) {
                        return;
                    }
                    GFMinimalNotification.make(mCoordinatorLayout, t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
                }
            });
        } else {
            if (isFinishing()) {
                return;
            }
            GFMinimalNotification.make(mCoordinatorLayout, R.string.error_user_profile, GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_WARNING).show();
        }
    }

    private void requestWorkout() {
        mService.requestWorkout(mUser.getId()).enqueue(new Callback<WCUser>() {
            @Override
            public void onResponse(Call<WCUser> call, Response<WCUser> response) {
                if (isFinishing()) {
                    return;
                }
                GFMinimalNotification.make(mCoordinatorLayout, R.string.workout_request_sent, GFMinimalNotification.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<WCUser> call, Throwable t) {
                if (isFinishing()) {
                    return;
                }
                GFMinimalNotification.make(mCoordinatorLayout, R.string.error_user_profile, GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_WARNING).show();
            }
        });
    }

    private void continueOn() {
        if (mIsNewUser) {
            startActivity(new Intent(this, WorkoutCompanionActivity.class));
            supportFinishAfterTransition();
        } else {
            hideFab();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    supportFinishAfterTransition();
                }
            }, AnimationUtils.DEFAULT_FAB_ANIM_DURATION);
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