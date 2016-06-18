package com.github.gfranks.workoutcompanion.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.R;

public class GymDetailsView extends LinearLayout {

    private TextView mTitle;
    private TextView mDescription;

    public GymDetailsView(Context context) {
        this(context, null);
    }

    public GymDetailsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GymDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GymDetailsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    public void setTitle(int titleResId) {
        mTitle.setText(titleResId);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    public void setTitleTextAppearance(int titleTextAppearance) {
        if (titleTextAppearance != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTitle.setTextAppearance(titleTextAppearance);
            } else {
                mTitle.setTextAppearance(getContext(), titleTextAppearance);
            }
        }
    }

    public void setDescription(int descriptionResId) {
        mDescription.setVisibility(View.VISIBLE);
        mDescription.setText(descriptionResId);
    }

    public void setDescription(String description) {
        mDescription.setVisibility(View.VISIBLE);
        mDescription.setText(description);
    }

    public void setDescription(CharSequence description) {
        mDescription.setVisibility(View.VISIBLE);
        mDescription.setText(description);
    }

    public void setDescriptionTextAppearance(int descriptionTextAppearance) {
        if (descriptionTextAppearance != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mDescription.setTextAppearance(descriptionTextAppearance);
            } else {
                mDescription.setTextAppearance(getContext(), descriptionTextAppearance);
            }
        }
    }

    public void setOnDescriptionClickListener(OnClickListener listener) {
        mDescription.setOnClickListener(listener);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        removeAllViews();
        setOrientation(VERTICAL);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme_background));

        float density = getContext().getResources().getDisplayMetrics().density;
        int leftRightPadding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int topBottomMargin = getResources().getDimensionPixelSize(R.dimen.gym_details_view_vertical_margin);

        View topDivider = new View(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (1F * density));
        topDivider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme_divider));
        topDivider.setLayoutParams(lp);
        addView(topDivider);

        mTitle = new TextView(getContext());
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mTitle.setLayoutParams(lp);
        mTitle.setPadding(leftRightPadding, topBottomMargin, leftRightPadding, 0);
        mTitle.setEnabled(false);
        setTitleTextAppearance(R.style.DefaultAppTheme_TextAppearance_SuperSmall);
        addView(mTitle);

        mDescription = new TextView(getContext());
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mDescription.setLayoutParams(lp);
        mDescription.setPadding(leftRightPadding, (int) (topBottomMargin*1.5), leftRightPadding, (int) (topBottomMargin*1.5));
        setDescriptionTextAppearance(R.style.TextAppearance_AppCompat_Small);
        mDescription.setVisibility(View.GONE);
        addView(mDescription);

        View bottomDivider = new View(getContext());
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (1F * density));
        bottomDivider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme_divider));
        bottomDivider.setLayoutParams(lp);
        addView(bottomDivider);

        TypedValue typedValue = new TypedValue();
        int[] descriptionTextColorAttr = new int[]{android.R.attr.textColorPrimary};
        int indexOfAttrTextColor = 0;
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, descriptionTextColorAttr);
        int descriptionTextColor = a.getColor(indexOfAttrTextColor, -1);
        a.recycle();
        if (descriptionTextColor != -1) {
            mDescription.setTextColor(descriptionTextColor);
        }

        if (attrs != null) {
            a = getContext().obtainStyledAttributes(attrs, R.styleable.GymDetailsView, defStyleAttr, 0);
            setTitle(a.getText(R.styleable.GymDetailsView_gdv_title));
            setDescription(a.getText(R.styleable.GymDetailsView_gdv_description));
            a.recycle();
        }
    }
}
