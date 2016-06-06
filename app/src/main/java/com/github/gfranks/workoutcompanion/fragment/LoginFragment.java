package com.github.gfranks.workoutcompanion.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.WorkoutCompanionActivity;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.notification.WCInAppMessageManagerConstants;
import com.github.gfranks.workoutcompanion.util.Utils;
import com.github.gfranks.workoutcompanion.util.ValidatorUtils;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.urbanairship.UAirship;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends BaseFragment implements Callback<WCUser>, TextView.OnEditorActionListener {

    public static final String TAG = "login_fragment";

    @Inject
    WorkoutCompanionService mService;
    @Inject
    AccountManager mAccountManager;

    @InjectView(R.id.log_in_header_title)
    View mTitle;
    @InjectView(R.id.log_in_header_subtitle)
    View mSubtitle;
    @InjectView(R.id.email)
    MaterialEditText mEmail;
    @InjectView(R.id.password)
    MaterialEditText mPassword;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmail.addValidator(ValidatorUtils.getEmailValidator(getContext()));
        mPassword.addValidator(ValidatorUtils.getNonEmptyValidator(getString(R.string.error_no_password)));
        mPassword.setOnEditorActionListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.login));

        if (mEmail.getText().toString().isEmpty()) {
            mEmail.setText(mAccountManager.getEmail());
        }
    }

    /**
     * ********************
     * View.OnClickListener
     * ********************
     */
    @OnClick(R.id.log_in)
    void login() {
        Utils.hideSoftKeyboard(getActivity());
        if (mEmail.validate() && mPassword.validate()) {
            mService.login(mEmail.getText().toString(), mPassword.getText().toString()).enqueue(this);
        } else {
            UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getWarningBuilder()
                    .setAlert(getString(R.string.error_login))
                    .create());
        }
    }

    @OnClick(R.id.create_account)
    void createAccount() {
        Utils.hideSoftKeyboard(getActivity());
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(getId(), CreateAccountFragment.newInstance(), CreateAccountFragment.TAG)
                .addToBackStack(CreateAccountFragment.TAG)
                .addSharedElement(mTitle, getString(R.string.transition_log_in_title))
                .addSharedElement(mSubtitle, getString(R.string.transition_log_in_subtitle))
                .commit();
    }

    @OnClick(R.id.forgot_password)
    void forgotPassword() {
        Utils.hideSoftKeyboard(getActivity());
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(getId(), ForgotPasswordFragment.newInstance(), ForgotPasswordFragment.TAG)
                .addToBackStack(ForgotPasswordFragment.TAG)
                .addSharedElement(mTitle, getString(R.string.transition_log_in_title))
                .addSharedElement(mSubtitle, getString(R.string.transition_log_in_subtitle))
                .commit();
    }

    /**
     * *******************************
     * TextView.OnEditorActionListener
     * *******************************
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            login();
            return true;
        }
        return false;
    }

    /**
     * ****************
     * Callback<WCUser>
     * ****************
     */
    @Override
    public void onResponse(Call<WCUser> call, Response<WCUser> response) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        mAccountManager.login(response.body());
        startActivity(new Intent(getActivity(), WorkoutCompanionActivity.class));
        getActivity().supportFinishAfterTransition();
    }

    @Override
    public void onFailure(Call<WCUser> call, Throwable t) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getErrorBuilder()
                .setAlert(t.getMessage())
                .create());
    }
}
