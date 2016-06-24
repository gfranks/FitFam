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

public class AgeSelectFragment extends BaseFragment implements SeekArc.OnSeekArcChangeListener {

    private static final String EXTRA_AGE = "age";

    @InjectView(R.id.age_seekbar)
    SeekArc mAgeSeekBar;
    @InjectView(R.id.age_seekbar_progress)
    TextView mAgeText;

    private int mAge;
    private boolean mEditMode;
    private OnAgeChangedListener mOnAgeChangedListener;

    public static AgeSelectFragment newInstance(int age, boolean isEditMode, OnAgeChangedListener onAgeChangedListener) {
        AgeSelectFragment fragment = new AgeSelectFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_AGE, age);
        fragment.setArguments(args);
        fragment.setOnAgeChangeListener(onAgeChangedListener);
        fragment.mEditMode = isEditMode;
        return fragment;
    }

    public static AgeSelectFragment newInstance(int age, boolean isEditMode) {
        return newInstance(age, isEditMode, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mAge = savedInstanceState.getInt(EXTRA_AGE, 0);
        } else if (getArguments() != null) {
            mAge = getArguments().getInt(EXTRA_AGE, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_age_select, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAgeSeekBar.setProgress(mAge);
        mAgeText.setText(getString(R.string.age_value, mAge));
        mAgeSeekBar.setOnSeekArcChangeListener(this);
    }

    /**
     * *******************************************
     * CircleSeekBar.OnCircleSeekBarChangeListener
     * *******************************************
     */
    @Override
    public void onProgressChanged(SeekArc circleSeekBar, int progress, boolean fromUser) {
        mAgeText.setText(getString(R.string.age_value, mAgeSeekBar.getProgress()));
        if (mOnAgeChangedListener != null) {
            mOnAgeChangedListener.onAgeChanged(this, progress);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekArc seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekArc seekBar) {
    }

    public void setOnAgeChangeListener(OnAgeChangedListener onAgeChangedListener) {
        mOnAgeChangedListener = onAgeChangedListener;
    }

    public void setAge(int age) {
        mAgeSeekBar.setProgress(age);
    }

    public int getAge() {
        return mAgeSeekBar.getProgress();
    }

    public void edit(boolean editMode) {
        mEditMode = editMode;
        mAgeSeekBar.setEnabled(mEditMode);
    }

    public interface OnAgeChangedListener {
        void onAgeChanged(AgeSelectFragment fragment, int age);
    }
}
