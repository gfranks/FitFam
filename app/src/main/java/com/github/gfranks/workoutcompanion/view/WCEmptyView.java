package com.github.gfranks.workoutcompanion.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WCEmptyView extends FrameLayout {

    @InjectView(R.id.empty_view_header_container)
    ViewGroup mHeaderContainer;
    @InjectView(R.id.empty_view_text_container)
    LinearLayout mEmptyTextContainer;
    @InjectView(R.id.empty_view_title)
    TextView mEmptyTitle;
    @InjectView(R.id.empty_view_subtitle)
    TextView mEmptySubtitle;
    @InjectView(R.id.empty_view_progress)
    ProgressBar mProgress;

    private View mHeader;

    public WCEmptyView(Context context) {
        this(context, null);
    }

    public WCEmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WCEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WCEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.mEmptyTitle = mEmptyTitle.getText().toString();
        ss.mEmptySubtitle = mEmptySubtitle.getText().toString();
        ss.mIsProgressDisplayed = mProgress.getVisibility() == View.VISIBLE;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        mEmptyTitle.setText(ss.mEmptyTitle);
        mEmptySubtitle.setText(ss.mEmptySubtitle);
        displayLoading(ss.mIsProgressDisplayed);
    }

    public View addEmptyHeader(int headerResId) {
        mHeaderContainer.setVisibility(View.VISIBLE);
        mEmptyTextContainer.setGravity(Gravity.TOP);
        mEmptyTextContainer.setPadding(mEmptyTextContainer.getPaddingLeft(), (int) (35.0F * getResources().getDisplayMetrics().density)
                , mEmptyTextContainer.getPaddingRight(), mEmptyTextContainer.getPaddingBottom());
        if (mHeaderContainer.getChildCount() != 0) {
            mHeaderContainer.removeAllViews();
        }

        mHeader = inflate(getContext(), headerResId, mHeaderContainer);
        return mHeader;
    }

    public void addEmptyHeader(View header) {
        mHeaderContainer.setVisibility(View.VISIBLE);
        mEmptyTextContainer.setGravity(Gravity.TOP);
        mEmptyTextContainer.setPadding(mEmptyTextContainer.getPaddingLeft(), (int) (35.0F * getResources().getDisplayMetrics().density)
                , mEmptyTextContainer.getPaddingRight(), mEmptyTextContainer.getPaddingBottom());
        if (mHeaderContainer.getChildCount() != 0) {
            mHeaderContainer.removeAllViews();
        }

        mHeader = header;
        mHeaderContainer.addView(mHeader);
    }

    public void setTitle(int textResId) {
        setTitle(getContext().getString(textResId));
    }

    public void setTitle(String text) {
        mEmptyTitle.setVisibility(View.VISIBLE);
        mEmptyTitle.setText(text);
        mEmptySubtitle.setPadding(0, (int) (10.0F * getResources().getDisplayMetrics().density), 0, 0);
    }

    public void setSubtitle(int textResId) {
        setSubtitle(getContext().getString(textResId));
    }

    public void setSubtitle(String text) {
        mEmptySubtitle.setVisibility(View.VISIBLE);
        mEmptySubtitle.setText(text);
    }

    public void setTitleTextAppearance(int textAppearanceResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mEmptyTitle.setTextAppearance(textAppearanceResId);
        } else {
            mEmptyTitle.setTextAppearance(getContext(), textAppearanceResId);
        }
    }

    public void setSubtitleTextAppearance(int textAppearanceResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mEmptySubtitle.setTextAppearance(textAppearanceResId);
        } else {
            mEmptySubtitle.setTextAppearance(getContext(), textAppearanceResId);
        }
    }

    public void displayLoading(boolean loading) {
        if (loading) {
            mEmptyTextContainer.setVisibility(View.INVISIBLE);
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mEmptyTextContainer.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.INVISIBLE);
        }
    }

    private void init() {
        inflate(getContext(), R.layout.layout_empty_view, this);
        ButterKnife.inject(this, this);
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

        String mEmptyTitle;
        String mEmptySubtitle;
        boolean mIsProgressDisplayed;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mEmptyTitle = in.readString();
            mEmptySubtitle = in.readString();
            mIsProgressDisplayed = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(mEmptyTitle);
            out.writeString(mEmptySubtitle);
            out.writeInt(mIsProgressDisplayed ? 1 : 0);
        }
    }
}
