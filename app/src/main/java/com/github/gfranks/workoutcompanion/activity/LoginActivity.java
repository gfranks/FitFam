package com.github.gfranks.workoutcompanion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.base.BaseActivity;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.fragment.LoginFragment;
import com.github.gfranks.workoutcompanion.manager.AccountManager;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity {

    @Inject
    AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (mAccountManager.isLoggedIn()) {
            WCUser user = mAccountManager.getUser();
            if (user.getFirstName() == null || user.getFirstName().isEmpty()
                    || user.getLastName() == null || user.getLastName().isEmpty()) {
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(WCUser.EXTRA_NEW, true);
                startActivity(intent);
            } else {
                startActivity(new Intent(this, WorkoutCompanionActivity.class));
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
