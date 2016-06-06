package com.github.gfranks.workoutcompanion.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;

public class EmptyView extends FrameLayout {

    private TextView mEmptyText;
    private ProgressBar mProgress;

    public EmptyView(Context context) {
        super(context);
        init(null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.mEmptyText = mEmptyText.getText().toString();
        ss.mIsProgressDisplayed = mProgress.getVisibility() == View.VISIBLE;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        mEmptyText.setText(ss.mEmptyText);
        displayLoading(ss.mIsProgressDisplayed);
    }

    public void setText(int textResId) {
        mEmptyText.setText(textResId);
    }

    public void setText(String text) {
        mEmptyText.setText(text);
    }

    public void displayLoading(boolean loading) {
        if (loading) {
            mEmptyText.setVisibility(View.INVISIBLE);
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mEmptyText.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.INVISIBLE);
        }
    }

    private void init(AttributeSet attrs) {
        removeAllViews();
        mEmptyText = new TextView(getContext(), attrs);
        mEmptyText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mEmptyText);

        mProgress = new ProgressBar(getContext(), attrs);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        mProgress.setLayoutParams(lp);
        mProgress.setVisibility(View.GONE);
        addView(mProgress);
    }

    static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        String mEmptyText;
        boolean mIsProgressDisplayed;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mEmptyText = in.readString();
            mIsProgressDisplayed = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(mEmptyText);
            out.writeInt(mIsProgressDisplayed ? 1 : 0);
        }
    }
}
