package com.github.gfranks.fitfam.activity;

import android.Manifest;
import android.animation.Animator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.transition.Transition;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.gfranks.fitfam.activity.base.BaseActivity;
import com.github.gfranks.fitfam.data.api.FitFamService;
import com.github.gfranks.fitfam.data.model.FFGym;
import com.github.gfranks.fitfam.data.model.FFUser;
import com.github.gfranks.fitfam.dialog.SelectGymDialog;
import com.github.gfranks.fitfam.fragment.ExerciseTypeFragment;
import com.github.gfranks.fitfam.fragment.WeightSelectFragment;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.util.ValidatorUtils;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.util.AnimationUtils;
import com.github.gfranks.fitfam.util.CropCircleTransformation;
import com.github.gfranks.fitfam.util.EndSheetBehavior;
import com.github.gfranks.fitfam.util.Utils;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends BaseActivity implements Callback<FFUser> {

    private static final String CAN_EDIT = "can_edit";
    private static final String EDIT_MODE = "edit_mode";
    private static final int REQUEST_CODE_PHONE_CALL = 1;

    @Inject
    FitFamService mService;
    @Inject
    AccountManager mAccountManager;
    @Inject
    Picasso mPicasso;

    @InjectView(R.id.image)
    ImageView mImage;
    @InjectView(R.id.set_public)
    CheckBox mPublicPrivate;
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

    private FFUser mUser;
    private boolean mIsNewUser, mCanEdit, mEditMode, mTransitioned, mExiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupTransitionListener();

        if (savedInstanceState != null) {
            mUser = savedInstanceState.getParcelable(FFUser.EXTRA);
            mEditMode = savedInstanceState.getBoolean(EDIT_MODE);
            mCanEdit = savedInstanceState.getBoolean(CAN_EDIT);
            mTransitioned = true;
        } else {
            if (getIntent().hasExtra(FFUser.EXTRA)) {
                mUser = getIntent().getParcelableExtra(FFUser.EXTRA);
                mCanEdit = mUser.equals(mAccountManager.getUser());
            } else {
                mUser = mAccountManager.getUser();
                mCanEdit = true;
            }

            mService.getUser(mUser.getId()).enqueue(this);
        }

        if (getIntent().hasExtra(FFUser.EXTRA_NEW)) {
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
        outState.putParcelable(FFUser.EXTRA, mUser);
        outState.putBoolean(EDIT_MODE, mEditMode);
        outState.putBoolean(CAN_EDIT, mCanEdit);
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
        mPublicPrivate.setEnabled(mEditMode);
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
                    public void onGymSelected(SelectGymDialog dialog, FFGym gym) {
                        mUser.setHomeGymId(gym.getPlace_id());
                        mUser.setHomeGym(gym.getName());
                        mHomeGym.setText(gym.getName());
                    }
                }).show();
                break;
        }
    }

    @OnLongClick(R.id.set_public)
    boolean onPublicPrivateLongClick(View v) {
        Toast toast;
        if (mPublicPrivate.isChecked()) {
            toast = Toast.makeText(this, R.string.set_private, Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(this, R.string.set_public, Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.TOP | Gravity.START, v.getLeft(), v.getBottom());
        toast.show();
        return true;
    }

    /**
     * ****************
     * Callback<FFUser>
     * ****************
     */
    @Override
    public void onResponse(Call<FFUser> call, Response<FFUser> response) {
        if (isFinishing()) {
            return;
        }
        mUser = response.body();
        initUser();
    }

    @Override
    public void onFailure(Call<FFUser> call, Throwable t) {
        if (isFinishing()) {
            return;
        }
        GFMinimalNotification.make(mCoordinatorLayout, t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();

        supportFinishAfterTransition();
    }

    private void initUser() {
        setTitle(mUser.getFullName() + (mCanEdit ? " - You" : ""));
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

        mPublicPrivate.setChecked(mUser.isPublic());
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
        exerciseTypeFragment.setExercises(mUser.getExercises(), mCanEdit);
    }

    private void saveInfo() {
        mUser.setPublic(mPublicPrivate.isChecked());
        mUser.setFirstName(mFirstName.getText().toString());
        mUser.setLastName(mLastName.getText().toString());
        mUser.setEmail(mEmail.getText().toString());
        mUser.setPhoneNumber(mPhoneNumber.getText().toString());
        mUser.setBirthday(mBirthday.getText().toString());
        mUser.setSex(mMale.isChecked() ? FFUser.MALE : FFUser.FEMALE);
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
            mService.updateUser(mUser.getId(), mUser).enqueue(new Callback<FFUser>() {
                @Override
                public void onResponse(Call<FFUser> call, Response<FFUser> response) {
                    if (isFinishing()) {
                        return;
                    }
                    if (mAccountManager.getUser().equals(response.body())) {
                        mAccountManager.setUser(response.body());
                    }
                    continueOn();
                }

                @Override
                public void onFailure(Call<FFUser> call, Throwable t) {
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
        mService.requestWorkout(mUser.getId()).enqueue(new Callback<FFUser>() {
            @Override
            public void onResponse(Call<FFUser> call, Response<FFUser> response) {
                if (isFinishing()) {
                    return;
                }
                GFMinimalNotification.make(mCoordinatorLayout, R.string.workout_request_sent, GFMinimalNotification.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<FFUser> call, Throwable t) {
                if (isFinishing()) {
                    return;
                }
                GFMinimalNotification.make(mCoordinatorLayout, R.string.error_user_profile, GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_WARNING).show();
            }
        });
    }

    private void makePhoneCall() {
        new MaterialDialog.Builder(this)
                .content(getString(R.string.call_prompt, mUser.getFullName(), mUser.getPhoneNumber()))
                .positiveText(R.string.action_call)
                .negativeText(R.string.action_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (ContextCompat.checkSelfPermission(UserProfileActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            String uri = "tel:" + mUser.getPhoneNumber();
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(uri));
                            startActivity(intent);
                        }
                    }
                });
    }

    private void continueOn() {
        if (mIsNewUser) {
            startActivity(new Intent(this, FitFamActivity.class));
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
        mFab.animate().scaleX(1).scaleY(1).setDuration(AnimationUtils.DEFAULT_FAB_ANIM_DURATION).setListener(new AnimationUtils.DefaultAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFab.setVisibility(View.VISIBLE);
            }
        }).start();
    }

    private void hideFab() {
        if (mFab.getScaleX() == 0 && mFab.getScaleY() == 0) {
            // handle case where user rotates to ensure we remove the fab
            mFab.setVisibility(View.GONE);
            return;
        }
        mFab.animate().scaleX(0).scaleY(0).setDuration(AnimationUtils.DEFAULT_FAB_ANIM_DURATION).setListener(new AnimationUtils.DefaultAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFab.setVisibility(View.GONE);
            }
        }).start();
    }
}