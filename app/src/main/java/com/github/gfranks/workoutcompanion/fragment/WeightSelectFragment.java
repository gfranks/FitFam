package com.github.gfranks.workoutcompanion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.view.SeekArc;

import butterknife.InjectView;

public class WeightSelectFragment extends BaseFragment implements SeekArc.OnSeekArcChangeListener {

    @InjectView(R.id.weight_seekbar)
    SeekArc mWeightSeekBar;
    @InjectView(R.id.weight_seekbar_progress)
    TextView mWeightText;

    private WCUser mUser;
    private boolean mEditMode;

    public static WeightSelectFragment newInstance(WCUser user) {
        WeightSelectFragment fragment = new WeightSelectFragment();
        Bundle args = new Bundle();
        args.putParcelable(WCUser.EXTRA, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mUser = savedInstanceState.getParcelable(WCUser.EXTRA);
        } else if (getArguments() != null) {
            mUser = getArguments().getParcelable(WCUser.EXTRA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weight_select, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWeightSeekBar.setOnSeekArcChangeListener(this);
        if (mUser != null) {
            setUser(mUser);
        }
    }

    /**
     * *******************************************
     * CircleSeekBar.OnCircleSeekBarChangeListener
     * *******************************************
     */
    @Override
    public void onProgressChanged(SeekArc circleSeekBar, int progress, boolean fromUser) {
        mWeightText.setText(getString(R.string.weight_value, mWeightSeekBar.getProgress()));
    }

    @Override
    public void onStopTrackingTouch(SeekArc seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekArc seekBar) {
    }

    public void setUser(WCUser user) {
        mUser = user;
        if (!isDetached() && getActivity() != null) {
            mWeightSeekBar.setProgress(mUser.getWeight());
            mWeightText.setText(getString(R.string.weight_value, mWeightSeekBar.getProgress()));
        }
    }

    public int getWeight() {
        return mWeightSeekBar.getProgress();
    }

    public void edit(boolean editMode) {
        mEditMode = editMode;
        mWeightSeekBar.setEnabled(mEditMode);
    }
}
