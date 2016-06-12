package com.github.gfranks.workoutcompanion.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;

public class EmptyView extends FrameLayout {

    private View mHeader;
    private LinearLayout mContainer;
    private TextView mEmptyTitle, mEmptySubtitle;
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

        ss.mEmptyTitle = mEmptyTitle.getText().toString();
        ss.mEmptySubtitle = mEmptySubtitle.getText().toString();
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

        mEmptyTitle.setText(ss.mEmptyTitle);
        mEmptySubtitle.setText(ss.mEmptySubtitle);
        displayLoading(ss.mIsProgressDisplayed);
    }

    public View addEmptyHeader(int headerResId) {
        ViewGroup headerRoot = ((ViewGroup) mContainer.getChildAt(0));
        headerRoot.setVisibility(View.VISIBLE);
        LinearLayout emptyTextRoot = ((LinearLayout) mContainer.getChildAt(1));
        emptyTextRoot.setGravity(Gravity.TOP);
        emptyTextRoot.setPadding(emptyTextRoot.getPaddingLeft(), (int) (35.0F * getResources().getDisplayMetrics().density)
                , emptyTextRoot.getPaddingRight(), 0);
        if (headerRoot.getChildCount() != 0) {
            headerRoot.removeAllViews();
        }

        mHeader = inflate(getContext(), headerResId, headerRoot);
        return mHeader;
    }

    public void addEmptyHeader(View header) {
        ViewGroup headerRoot = ((ViewGroup) mContainer.getChildAt(0));
        headerRoot.setVisibility(View.VISIBLE);
        LinearLayout emptyTextRoot = ((LinearLayout) mContainer.getChildAt(1));
        emptyTextRoot.setGravity(Gravity.TOP);
        emptyTextRoot.setPadding(emptyTextRoot.getPaddingLeft(), (int) (35.0F * getResources().getDisplayMetrics().density)
                , emptyTextRoot.getPaddingRight(), 0);
        if (headerRoot.getChildCount() != 0) {
            headerRoot.removeAllViews();
        }

        mHeader = header;
        headerRoot.addView(mHeader);
    }

    public void setTitle(int textResId) {
        mEmptyTitle.setText(textResId);
    }

    public void setTitle(String text) {
        mEmptyTitle.setText(text);
    }

    public void setSubtitle(int textResId) {
        mEmptySubtitle.setText(textResId);
    }

    public void setSubtitle(String text) {
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
            mContainer.setVisibility(View.INVISIBLE);
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mContainer.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.INVISIBLE);
        }
    }

    private void init(AttributeSet attrs) {
        removeAllViews();

        float density = getResources().getDisplayMetrics().density;
        int fiveDp = (int) (5.0F * density);
        int tenDp = (int) (10.0F * density);

        mContainer = new LinearLayout(getContext());
        mContainer.setOrientation(LinearLayout.VERTICAL);
        mContainer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        View headerContainer = new RelativeLayout(getContext());
        headerContainer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        headerContainer.setPadding(0, fiveDp, 0, fiveDp);
        headerContainer.setVisibility(View.GONE);
        mContainer.addView(headerContainer);

        LinearLayout emptyTextContainer = new LinearLayout(getContext(), attrs);
        emptyTextContainer.setOrientation(LinearLayout.VERTICAL);
        emptyTextContainer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyTextContainer.setGravity(Gravity.CENTER_VERTICAL);
        emptyTextContainer.setPadding(tenDp, 0, tenDp, 0);
        mEmptyTitle = new TextView(getContext());
        mEmptyTitle.setGravity(Gravity.CENTER);
        emptyTextContainer.addView(mEmptyTitle);
        mEmptySubtitle = new TextView(getContext());
        mEmptySubtitle.setGravity(Gravity.CENTER);
        mEmptySubtitle.setPadding(0, tenDp, 0, 0);
        emptyTextContainer.addView(mEmptySubtitle);
        setTitleTextAppearance(R.style.TextAppearance_AppCompat_Headline);
        setSubtitleTextAppearance(R.style.TextAppearance_AppCompat_Subhead);
        mContainer.addView(emptyTextContainer);
        addView(mContainer);

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
