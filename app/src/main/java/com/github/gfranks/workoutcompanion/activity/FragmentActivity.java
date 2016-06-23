package com.github.gfranks.workoutcompanion.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.base.BaseActivity;

public class FragmentActivity extends BaseActivity {

    public static final String FRAGMENT = "fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        if (!getIntent().hasExtra(FRAGMENT)) {
            throw new IllegalStateException("Must pass the name of the fragment you wish to instantiate in this container");
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
            try {
                fragment = Fragment.instantiate(this, Class.forName(getIntent().getStringExtra(FRAGMENT)).getName(), getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_fragment_content, fragment)
                        .commit();
            } catch (ClassNotFoundException exception) {
                supportFinishAfterTransition();
            }
        }
    }
}
