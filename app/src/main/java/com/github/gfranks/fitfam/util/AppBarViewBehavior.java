package com.github.gfranks.fitfam.util;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class AppBarViewBehavior extends CoordinatorLayout.Behavior<View> {

    /**
     * Default constructor for instantiating AppBarViewBehaviors.
     */
    public AppBarViewBehavior() {
    }

    /**
     * Default constructor for inflating AppBarViewBehaviors from layout.
     *
     * @param context The {@link Context}.
     * @param attrs   The {@link AttributeSet}.
     */
    public AppBarViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent,
                                   View child, View dependency) {
        return false;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child,
                                          View dependency) {
        if (dependency instanceof AppBarLayout) {
            // If we're depending on an AppBarLayout we will show/hide it automatically
            // if the View is anchored to the AppBarLayout
            updateViewVisibility(parent, (AppBarLayout) dependency, child);
        }
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child,
                                 int layoutDirection) {
        // First, lets make sure that the visibility of the view is consistent
        final List<View> dependencies = parent.getDependencies(child);
        for (int i = 0, count = dependencies.size(); i < count; i++) {
            final View dependency = dependencies.get(i);
            if (dependency instanceof AppBarLayout
                    && updateViewVisibility(parent, (AppBarLayout) dependency, child)) {
                break;
            }
        }
        // Now let the CoordinatorLayout lay out the view
        parent.onLayoutChild(child, layoutDirection);
        return true;
    }

    private boolean updateViewVisibility(CoordinatorLayout parent,
                                         AppBarLayout appBarLayout, final View child) {
        final CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (lp.getAnchorId() != appBarLayout.getId()) {
            return false;
        }

        if (appBarLayout.getBottom() <= getMinimumHeightForVisibleOverlappingContent(appBarLayout)) {
            child.animate().scaleX(0).scaleY(0).setDuration(200).start();
        } else {
            child.animate().scaleX(1).scaleY(1).setDuration(200).start();
        }
        return true;
    }

    private int getMinimumHeightForVisibleOverlappingContent(AppBarLayout appBarLayout) {
        final int minHeight = ViewCompat.getMinimumHeight(appBarLayout);
        if (minHeight != 0) {
            // If this layout has a min height, use it (doubled)
            return (int) (minHeight * 2.5);
        }

        // Otherwise, we'll use twice the min height of our last child
        final int childCount = appBarLayout.getChildCount();
        return childCount >= 1
                ? (int) (ViewCompat.getMinimumHeight(appBarLayout.getChildAt(childCount - 1)) * 2.5)
                : 0;
    }
}