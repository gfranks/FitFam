package com.github.gfranks.fitfam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.fragment.base.BaseFragment;
import com.github.gfranks.fitfam.util.Utils;
import com.github.gfranks.fitfam.util.ValidatorUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.InjectView;
import butterknife.OnClick;

public class ForgotPasswordFragment extends BaseFragment implements TextView.OnEditorActionListener {

    public static final String TAG = "forgot_password_fragment";

    @InjectView(R.id.email)
    MaterialEditText mEmail;

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmail.addValidator(ValidatorUtils.getEmailValidator(getContext()));
        mEmail.setOnEditorActionListener(this);
    }

    /**
     * *******************************
     * TextView.OnEditorActionListener
     * *******************************
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            sendPasswordResetLink();
            return true;
        }
        return false;
    }


    /**
     * ********************
     * View.OnClickListener
     * ********************
     */
    @OnClick(R.id.back_to_login)
    void backToLogin() {
        Utils.hideSoftKeyboard(getActivity());
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @OnClick(R.id.forgot_password)
    void sendPasswordResetLink() {

    }
}
