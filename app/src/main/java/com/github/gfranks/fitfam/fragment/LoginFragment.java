package com.github.gfranks.fitfam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.github.gfranks.fitfam.activity.FitFamActivity;
import com.github.gfranks.fitfam.data.api.FitFamService;
import com.github.gfranks.fitfam.data.model.FFUser;
import com.github.gfranks.fitfam.fragment.base.BaseFragment;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.util.ValidatorUtils;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.util.Utils;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends BaseFragment implements Callback<FFUser>, TextView.OnEditorActionListener {

    public static final String TAG = "login_fragment";

    @Inject
    FitFamService mService;
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
            GFMinimalNotification.make(getView(), R.string.error_login, GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_WARNING).show();
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
     * Callback<FFUser>
     * ****************
     */
    @Override
    public void onResponse(Call<FFUser> call, Response<FFUser> response) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        mAccountManager.login(response.body());
        startActivity(new Intent(getActivity(), FitFamActivity.class));
        getActivity().supportFinishAfterTransition();
    }

    @Override
    public void onFailure(Call<FFUser> call, Throwable t) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        GFMinimalNotification.make(getView(), t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_ERROR).show();
    }
}
