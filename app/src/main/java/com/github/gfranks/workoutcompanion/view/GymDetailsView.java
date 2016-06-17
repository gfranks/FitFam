package com.github.gfranks.workoutcompanion.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
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
        mDescription.setText(descriptionResId);
    }

    public void setDescription(String description) {
        mDescription.setText(description);
    }

    public void setDescription(CharSequence description) {
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

        float density = getContext().getResources().getDisplayMetrics().density;
        int topBottomMargin = (int) (5F * density);

        setOrientation(VERTICAL);
        mTitle = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mTitle.setLayoutParams(lp);
        setTitleTextAppearance(R.style.DefaultAppTheme_TextAppearance_SuperSmall);
        mTitle.setEnabled(false);
        addView(mTitle, 0);

        mDescription = new TextView(getContext());
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, topBottomMargin, 0, topBottomMargin);
        mDescription.setLayoutParams(lp);
        setDescriptionTextAppearance(R.style.TextAppearance_AppCompat_Small);
        addView(mDescription);

        TypedValue typedValue = new TypedValue();
        int[] descriptionTextColorAttr = new int[]{android.R.attr.textColorPrimary};
        int indexOfAttrTextColor = 0;
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, descriptionTextColorAttr);
        int descriptionTextColor = a.getColor(indexOfAttrTextColor, -1);
        a.recycle();
        if (descriptionTextColor != -1) {
            mDescription.setTextColor(descriptionTextColor);
        }

        topBottomMargin = (int) (3F * density);
        View divider = new View(getContext());
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (1F * density));
        divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme_divider));
        lp.setMargins(0, topBottomMargin, 0, topBottomMargin);
        divider.setLayoutParams(lp);
        divider.setEnabled(false);
        addView(divider);

        if (attrs != null) {
            a = getContext().obtainStyledAttributes(attrs, R.styleable.GymDetailsView, defStyleAttr, 0);
            setTitle(a.getText(R.styleable.GymDetailsView_gdv_title));
            setDescription(a.getText(R.styleable.GymDetailsView_gdv_description));
            a.recycle();
        }
    }
}
