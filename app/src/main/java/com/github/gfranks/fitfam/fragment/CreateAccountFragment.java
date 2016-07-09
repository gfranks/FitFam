package com.github.gfranks.fitfam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.github.gfranks.fitfam.data.api.FitFamService;
import com.github.gfranks.fitfam.fragment.base.BaseFragment;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.activity.UserProfileActivity;
import com.github.gfranks.fitfam.data.model.FFUser;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.util.Utils;
import com.github.gfranks.fitfam.util.ValidatorUtils;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountFragment extends BaseFragment implements Callback<FFUser>, TextView.OnEditorActionListener {

    public static final String TAG = "create_account_fragment";

    @Inject
    FitFamService mService;
    @Inject
    AccountManager mAccountManager;

    @InjectView(R.id.email)
    MaterialEditText mEmail;
    @InjectView(R.id.password)
    MaterialEditText mPassword;
    @InjectView(R.id.confirm_password)
    MaterialEditText mConfirmPassword;

    public static CreateAccountFragment newInstance() {
        return new CreateAccountFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmail.addValidator(ValidatorUtils.getEmailValidator(getContext()));
        mPassword.addValidator(new METValidator(getString(R.string.error_no_password_create)) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return !isEmpty;
            }
        });
        mConfirmPassword.addValidator(new METValidator(getString(R.string.error_no_password_create)) {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return !isEmpty && text.toString().equals(mPassword.getText().toString());
            }
        });
        mConfirmPassword.setOnEditorActionListener(this);
    }

    /**
     * ********************
     * View.OnClickListener
     * ********************
     */
    @OnClick(R.id.create_account)
    void createAccount() {
        Utils.hideSoftKeyboard(getActivity());
        if (mEmail.validate() && mPassword.validate() && mConfirmPassword.validate()) {
            mService.createAccount(mEmail.getText().toString(), mPassword.getText().toString()).enqueue(this);
        } else {
            GFMinimalNotification.make(getView(), R.string.error_create_account, GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_WARNING).show();
        }
    }

    @OnClick(R.id.back_to_login)
    void backToLogin() {
        Utils.hideSoftKeyboard(getActivity());
        getActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * *******************************
     * TextView.OnEditorActionListener
     * *******************************
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            createAccount();
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
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        intent.putExtra(FFUser.EXTRA_NEW, true);
        startActivity(intent);
        getActivity().supportFinishAfterTransition();
    }

    @Override
    public void onFailure(Call<FFUser> call, Throwable t) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        GFMinimalNotification.make(getView(), t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_WARNING).show();
    }
}
