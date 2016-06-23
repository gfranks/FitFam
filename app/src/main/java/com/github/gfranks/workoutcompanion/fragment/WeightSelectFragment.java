package com.github.gfranks.workoutcompanion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.view.SeekArc;

import butterknife.InjectView;

public class WeightSelectFragment extends BaseFragment implements SeekArc.OnSeekArcChangeListener {

    private static final String EXTRA_WEIGHT = "weight";

    @InjectView(R.id.weight_seekbar)
    SeekArc mWeightSeekBar;
    @InjectView(R.id.weight_seekbar_progress)
    TextView mWeightText;

    private int mWeight;
    private boolean mEditMode;
    private OnWeightChangeListener mOnWeightChangeListener;

    public static WeightSelectFragment newInstance(int weight, boolean isEditMode, OnWeightChangeListener onWeightChangeListener) {
        WeightSelectFragment fragment = new WeightSelectFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_WEIGHT, weight);
        fragment.setArguments(args);
        fragment.setOnWeightChangeListener(onWeightChangeListener);
        fragment.mEditMode = isEditMode;
        return fragment;
    }

    public static WeightSelectFragment newInstance(int weight, boolean isEditMode) {
        return newInstance(weight, isEditMode, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mWeight = savedInstanceState.getInt(EXTRA_WEIGHT, 0);
        } else if (getArguments() != null) {
            mWeight = getArguments().getInt(EXTRA_WEIGHT, 0);
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

        mWeightSeekBar.setProgress(mWeight);
        mWeightText.setText(getString(R.string.weight_value, mWeight));
        mWeightSeekBar.setOnSeekArcChangeListener(this);
    }

    /**
     * *******************************************
     * CircleSeekBar.OnCircleSeekBarChangeListener
     * *******************************************
     */
    @Override
    public void onProgressChanged(SeekArc circleSeekBar, int progress, boolean fromUser) {
        mWeightText.setText(getString(R.string.weight_value, mWeightSeekBar.getProgress()));
        if (mOnWeightChangeListener != null) {
            mOnWeightChangeListener.onWeightChanged(this, progress);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekArc seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekArc seekBar) {
    }

    public void setOnWeightChangeListener(OnWeightChangeListener onWeightChangeListener) {
        mOnWeightChangeListener = onWeightChangeListener;
    }

    public void setWeight(int weight) {
        mWeightSeekBar.setProgress(weight);
    }

    public int getWeight() {
        return mWeightSeekBar.getProgress();
    }

    public void edit(boolean editMode) {
        mEditMode = editMode;
        mWeightSeekBar.setEnabled(mEditMode);
    }

    public interface OnWeightChangeListener {
        void onWeightChanged(WeightSelectFragment fragment, int weight);
    }
}
