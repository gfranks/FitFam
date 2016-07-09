package com.github.gfranks.fitfam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.gfranks.fitfam.data.model.FFUser;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.activity.base.BaseActivity;
import com.github.gfranks.fitfam.fragment.LoginFragment;
import com.github.gfranks.fitfam.manager.AccountManager;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity {

    @Inject
    AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (mAccountManager.isLoggedIn()) {
            FFUser user = mAccountManager.getUser();
            if (user.getFirstName() == null || user.getFirstName().isEmpty()
                    || user.getLastName() == null || user.getLastName().isEmpty()) {
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(FFUser.EXTRA_NEW, true);
                startActivity(intent);
            } else {
                startActivity(new Intent(this, FitFamActivity.class));
            }
            supportFinishAfterTransition();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_fragment_content);
        if (fragment != null) {
            if (fragment.isDetached()) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .attach(fragment)
                        .commit();
            } else if (fragment.getView() == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_fragment_content, fragment, fragment.getTag())
                        .commit();
            }
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_fragment_content, LoginFragment.newInstance(), LoginFragment.TAG)
                    .addToBackStack(LoginFragment.TAG)
                    .commit();
        }
    }
}
