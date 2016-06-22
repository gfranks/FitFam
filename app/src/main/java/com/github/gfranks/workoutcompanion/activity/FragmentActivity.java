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

        try {
            Fragment fragment = Fragment.instantiate(this, Class.forName(getIntent().getStringExtra(FRAGMENT)).getName(), getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_content, fragment)
                    .commit();
        } catch (ClassNotFoundException exception) {
            supportFinishAfterTransition();
        }
    }
}
