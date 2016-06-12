package com.github.gfranks.workoutcompanion.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.github.gfranks.workoutcompanion.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

/**
 * An interaction behavior plugin for a child view of {@link CoordinatorLayout} to make it work as
 * a end sheet.
 */
public class EndSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    /**
     * The end sheet is dragging.
     */
    public static final int STATE_DRAGGING = 1;
    /**
     * The end sheet is settling.
     */
    public static final int STATE_SETTLING = 2;
    /**
     * The end sheet is expanded.
     */
    public static final int STATE_EXPANDED = 3;
    /**
     * The end sheet is collapsed.
     */
    public static final int STATE_COLLAPSED = 4;
    /**
     * The end sheet is hidden.
     */
    public static final int STATE_HIDDEN = 5;
    private static final float HIDE_THRESHOLD = 0.5f;
    private static final float HIDE_FRICTION = 0.1f;
    private float mMaximumVelocity;
    private int mPeekWidth;
    private int mMinOffset;
    private int mMaxOffset;
    private boolean mHideable;
    @State
    private int mState = STATE_COLLAPSED;
    private ViewDragHelper mViewDragHelper;
    private boolean mIgnoreEvents;
    private int mLastNestedScrollDx;
    private boolean mNestedScrolled;
    private int mParentWidth;
    private WeakReference<V> mViewRef;
    private WeakReference<View> mNestedScrollingChildRef;
    private EndSheetCallback mCallback;
    private VelocityTracker mVelocityTracker;
    private int mActivePointerId;
    private int mInitialX;
    private boolean mTouchingScrollingChild;
    private final ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (mState == STATE_DRAGGING) {
                return false;
            }
            if (mTouchingScrollingChild) {
                return false;
            }
            if (mState == STATE_EXPANDED && mActivePointerId == pointerId) {
                View scroll = mNestedScrollingChildRef.get();
                if (scroll != null && ViewCompat.canScrollHorizontally(scroll, -1)) {
                    // Let the content scroll up
                    return false;
                }
            }
            return mViewRef != null && mViewRef.get() == child;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            dispatchOnSlide(left);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == ViewDragHelper.STATE_DRAGGING) {
                setStateInternal(STATE_DRAGGING);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int left;
            @State int targetState;
            if (xvel < 0) { // Moving left
                left = mMinOffset;
                targetState = STATE_EXPANDED;
            } else if (mHideable && shouldHide(releasedChild, xvel)) {
                left = mParentWidth;
                targetState = STATE_HIDDEN;
            } else if (xvel == 0.f) {
                int currentLeft = releasedChild.getLeft();
                if (Math.abs(currentLeft - mMinOffset) < Math.abs(currentLeft - mMaxOffset)) {
                    left = mMinOffset;
                    targetState = STATE_EXPANDED;
                } else {
                    left = mMaxOffset;
                    targetState = STATE_COLLAPSED;
                }
            } else {
                left = mMaxOffset;
                targetState = STATE_COLLAPSED;
            }
            if (mViewDragHelper.settleCapturedViewAt(left, releasedChild.getTop())) {
                setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(releasedChild,
                        new SettleRunnable(releasedChild, targetState));
            } else {
                setStateInternal(targetState);
            }
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return child.getTop();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return constrain(left, mMinOffset, mHideable ? mParentWidth : mMaxOffset);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            if (mHideable) {
                return mParentWidth - mMinOffset;
            } else {
                return mMaxOffset - mMinOffset;
            }
        }

        int constrain(int amount, int low, int high) {
            return amount < low ? low : (amount > high ? high : amount);
        }
    };

    /**
     * Default constructor for instantiating endSheetBehaviors.
     */
    public EndSheetBehavior() {
    }

    /**
     * Default constructor for inflating endSheetBehaviors from layout.
     *
     * @param context The {@link Context}.
     * @param attrs   The {@link AttributeSet}.
     */
    public EndSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EndSheetBehavior);
        setPeekWidth(a.getDimensionPixelSize(R.styleable.EndSheetBehavior_esb_peekWidth, 0));
        setHideable(a.getBoolean(R.styleable.EndSheetBehavior_esb_hideable, false));
        a.recycle();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    /**
     * A utility function to get the {@link EndSheetBehavior} associated with the {@code view}.
     *
     * @param view The {@link View} with {@link EndSheetBehavior}.
     * @return The {@link EndSheetBehavior} associated with the {@code view}.
     */
    @SuppressWarnings("unchecked")
    public static <V extends View> EndSheetBehavior<V> from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof EndSheetBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with endSheetBehavior");
        }
        return (EndSheetBehavior<V>) behavior;
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        return new SavedState(super.onSaveInstanceState(parent, child), mState);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, V child, Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        // Intermediate states are restored as collapsed state
        if (ss.state == STATE_DRAGGING || ss.state == STATE_SETTLING) {
            mState = STATE_COLLAPSED;
        } else {
            mState = ss.state;
        }
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        // First let the parent lay it out
        if (mState != STATE_DRAGGING && mState != STATE_SETTLING) {
            if (ViewCompat.getFitsSystemWindows(parent) &&
                    !ViewCompat.getFitsSystemWindows(child)) {
                ViewCompat.setFitsSystemWindows(child, true);
            }
            parent.onLayoutChild(child, layoutDirection);
        }
        // Offset the end sheet
        mParentWidth = parent.getWidth();
        mMinOffset = Math.max(0, mParentWidth - child.getWidth());
        mMaxOffset = Math.max(mParentWidth - mPeekWidth, mMinOffset);
        if (mState == STATE_EXPANDED) {
            ViewCompat.offsetLeftAndRight(child, mMinOffset);
        } else if (mHideable && mState == STATE_HIDDEN) {
            ViewCompat.offsetLeftAndRight(child, mParentWidth);
        } else if (mState == STATE_COLLAPSED) {
            ViewCompat.offsetLeftAndRight(child, mMaxOffset);
        }
        if (mViewDragHelper == null) {
            mViewDragHelper = ViewDragHelper.create(parent, mDragCallback);
        }
        mViewRef = new WeakReference<>(child);
        mNestedScrollingChildRef = new WeakReference<>(findScrollingChild(child));
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(event);
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchingScrollingChild = false;
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                // Reset the ignore flag
                if (mIgnoreEvents) {
                    mIgnoreEvents = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mInitialX = (int) event.getX();
                int initialY = (int) event.getY();
                View scroll = mNestedScrollingChildRef.get();
                if (scroll != null && parent.isPointInChildBounds(scroll, mInitialX, initialY)) {
                    mActivePointerId = event.getPointerId(event.getActionIndex());
                    mTouchingScrollingChild = true;
                }
                mIgnoreEvents = mActivePointerId == MotionEvent.INVALID_POINTER_ID &&
                        !parent.isPointInChildBounds(child, mInitialX, initialY);
                break;
        }
        if (!mIgnoreEvents && mViewDragHelper.shouldInterceptTouchEvent(event)) {
            return true;
        }
        // We have to handle cases that the ViewDragHelper does not capture the end sheet because
        // it is not the top most view of its parent. This is not necessary when the touch event is
        // happening over the scrolling content as nested scrolling logic handles that case.
        View scroll = mNestedScrollingChildRef.get();
        return action == MotionEvent.ACTION_MOVE && scroll != null &&
                !mIgnoreEvents && mState != STATE_DRAGGING &&
                !parent.isPointInChildBounds(scroll, (int) event.getX(), (int) event.getY()) &&
                Math.abs(mInitialX - event.getX()) > mViewDragHelper.getTouchSlop();
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(event);
        if (mState == STATE_DRAGGING && action == MotionEvent.ACTION_DOWN) {
            return true;
        }
        mViewDragHelper.processTouchEvent(event);
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        // The ViewDragHelper tries to capture only the top-most View. We have to explicitly tell it
        // to capture the end sheet in case it is not captured and the touch slop is passed.
        if (action == MotionEvent.ACTION_MOVE && !mIgnoreEvents) {
            if (Math.abs(mInitialX - event.getX()) > mViewDragHelper.getTouchSlop()) {
                mViewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
            }
        }
        return !mIgnoreEvents;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        mLastNestedScrollDx = 0;
        mNestedScrolled = false;
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx,
                                  int dy, int[] consumed) {
        View scrollingChild = mNestedScrollingChildRef.get();
        if (target != scrollingChild) {
            return;
        }
        int currentLeft = child.getLeft();
        int newLeft = currentLeft - dx;
        if (dx > 0) { // Leftward
            if (newLeft < mMinOffset) {
                consumed[1] = currentLeft - mMinOffset;
                ViewCompat.offsetLeftAndRight(child, -consumed[1]);
                setStateInternal(STATE_EXPANDED);
            } else {
                consumed[1] = dx;
                ViewCompat.offsetLeftAndRight(child, -dx);
                setStateInternal(STATE_DRAGGING);
            }
        } else if (dx < 0) { // Rightward
            if (!ViewCompat.canScrollHorizontally(target, -1)) {
                if (newLeft <= mMaxOffset || mHideable) {
                    consumed[1] = dx;
                    ViewCompat.offsetLeftAndRight(child, -dx);
                    setStateInternal(STATE_DRAGGING);
                } else {
                    consumed[1] = currentLeft - mMaxOffset;
                    ViewCompat.offsetLeftAndRight(child, -consumed[1]);
                    setStateInternal(STATE_COLLAPSED);
                }
            }
        }
        dispatchOnSlide(child.getLeft());
        mLastNestedScrollDx = dx;
        mNestedScrolled = true;
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        if (child.getLeft() == mMinOffset) {
            setStateInternal(STATE_EXPANDED);
            return;
        }
        if (target != mNestedScrollingChildRef.get() || !mNestedScrolled) {
            return;
        }
        int left;
        int targetState;
        if (mLastNestedScrollDx > 0) {
            left = mMinOffset;
            targetState = STATE_EXPANDED;
        } else if (mHideable && shouldHide(child, getXVelocity())) {
            left = mParentWidth;
            targetState = STATE_HIDDEN;
        } else if (mLastNestedScrollDx == 0) {
            int currentLeft = child.getLeft();
            if (Math.abs(currentLeft - mMinOffset) < Math.abs(currentLeft - mMaxOffset)) {
                left = mMinOffset;
                targetState = STATE_EXPANDED;
            } else {
                left = mMaxOffset;
                targetState = STATE_COLLAPSED;
            }
        } else {
            left = mMaxOffset;
            targetState = STATE_COLLAPSED;
        }
        if (mViewDragHelper.smoothSlideViewTo(child, left, child.getTop())) {
            setStateInternal(STATE_SETTLING);
            ViewCompat.postOnAnimation(child, new SettleRunnable(child, targetState));
        } else {
            setStateInternal(targetState);
        }
        mNestedScrolled = false;
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target,
                                    float velocityX, float velocityY) {
        return target == mNestedScrollingChildRef.get() &&
                (mState != STATE_EXPANDED ||
                        super.onNestedPreFling(coordinatorLayout, child, target,
                                velocityX, velocityY));
    }

    /**
     * Gets the width of the end sheet when it is collapsed.
     *
     * @return The width of the collapsed end sheet.
     * @attr ref R.styleable#EndSheetBehavior_peekWidth
     */
    public final int getPeekWidth() {
        return mPeekWidth;
    }

    /**
     * Sets the width of the end sheet when it is collapsed.
     *
     * @param peekWidth The width of the collapsed end sheet in pixels.
     * @attr ref R.styleable#EndSheetBehavior_peekWidth
     */
    public final void setPeekWidth(int peekWidth) {
        mPeekWidth = Math.max(0, peekWidth);
        mMaxOffset = mParentWidth - peekWidth;
    }

    /**
     * Gets whether this end sheet can hide when it is swiped down.
     *
     * @return {@code true} if this end sheet can hide.
     * @attr ref R.styleable#EndSheetBehavior_hideable
     */
    public boolean isHideable() {
        return mHideable;
    }

    /**
     * Sets whether this end sheet can hide when it is swiped down.
     *
     * @param hideable {@code true} to make this end sheet hideable.
     * @attr ref R.styleable#EndSheetBehavior_hideable
     */
    public void setHideable(boolean hideable) {
        mHideable = hideable;
    }

    /**
     * Sets a callback to be notified of end sheet events.
     *
     * @param callback The callback to notify when end sheet events occur.
     */
    public void setEndSheetCallback(EndSheetCallback callback) {
        mCallback = callback;
    }

    /**
     * Gets the current state of the end sheet.
     *
     * @return One of {@link #STATE_EXPANDED}, {@link #STATE_COLLAPSED}, {@link #STATE_DRAGGING},
     * and {@link #STATE_SETTLING}.
     */
    @State
    public final int getState() {
        return mState;
    }

    /**
     * Sets the state of the end sheet. The end sheet will transition to that state with
     * animation.
     *
     * @param state One of {@link #STATE_COLLAPSED}, {@link #STATE_EXPANDED}, or
     *              {@link #STATE_HIDDEN}.
     */
    public final void setState(@State int state) {
        if (state == mState) {
            return;
        }
        if (mViewRef == null) {
            // The view is not laid out yet; modify mState and let onLayoutChild handle it later
            if (state == STATE_COLLAPSED || state == STATE_EXPANDED ||
                    (mHideable && state == STATE_HIDDEN)) {
                mState = state;
            }
            return;
        }
        V child = mViewRef.get();
        if (child == null) {
            return;
        }
        int left;
        if (state == STATE_COLLAPSED) {
            left = mMaxOffset;
        } else if (state == STATE_EXPANDED) {
            left = mMinOffset;
        } else if (mHideable && state == STATE_HIDDEN) {
            left = mParentWidth;
        } else {
            throw new IllegalArgumentException("Illegal state argument: " + state);
        }
        setStateInternal(STATE_SETTLING);
        if (mViewDragHelper.smoothSlideViewTo(child, left, child.getTop())) {
            ViewCompat.postOnAnimation(child, new SettleRunnable(child, state));
        }
    }

    private void setStateInternal(@State int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        View endSheet = mViewRef.get();
        if (endSheet != null && mCallback != null) {
            mCallback.onStateChanged(endSheet, state);
        }
    }

    private void reset() {
        mActivePointerId = ViewDragHelper.INVALID_POINTER;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private boolean shouldHide(View child, float xvel) {
        if (child.getLeft() < mMaxOffset) {
            // It should not hide, but collapse.
            return false;
        }
        final float newLeft = child.getLeft() + xvel * HIDE_FRICTION;
        return Math.abs(newLeft - mMaxOffset) / (float) mPeekWidth > HIDE_THRESHOLD;
    }

    private View findScrollingChild(View view) {
        if (view instanceof NestedScrollingChild) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0, count = group.getChildCount(); i < count; i++) {
                View scrollingChild = findScrollingChild(group.getChildAt(i));
                if (scrollingChild != null) {
                    return scrollingChild;
                }
            }
        }
        return null;
    }

    private float getXVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        return VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId);
    }

    private void dispatchOnSlide(int left) {
        View endSheet = mViewRef.get();
        if (endSheet != null && mCallback != null) {
            if (left > mMaxOffset) {
                mCallback.onSlide(endSheet, (float) (mMaxOffset - left) / mPeekWidth);
            } else {
                mCallback.onSlide(endSheet,
                        (float) (mMaxOffset - left) / ((mMaxOffset - mMinOffset)));
            }
        }
    }

    /**
     * @hide
     */
    @IntDef({STATE_EXPANDED, STATE_COLLAPSED, STATE_DRAGGING, STATE_SETTLING, STATE_HIDDEN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    /**
     * Callback for monitoring events about end sheets.
     */
    public interface EndSheetCallback {

        /**
         * Called when the end sheet changes its state.
         *
         * @param endSheet The end sheet view.
         * @param newState The new state. This will be one of {@link #STATE_DRAGGING},
         *                 {@link #STATE_SETTLING}, {@link #STATE_EXPANDED},
         *                 {@link #STATE_COLLAPSED}, or {@link #STATE_HIDDEN}.
         */
        void onStateChanged(@NonNull View endSheet, @State int newState);

        /**
         * Called when the end sheet is being dragged.
         *
         * @param endSheet    The end sheet view.
         * @param slideOffset The new offset of this end sheet within its range, from 0 to 1
         *                    when it is moving upward, and from 0 to -1 when it moving downward.
         */
        void onSlide(@NonNull View endSheet, float slideOffset);
    }

    protected static class SavedState extends View.BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        @State
        final int state;

        public SavedState(Parcel source) {
            super(source);
            //noinspection ResourceType
            state = source.readInt();
        }

        public SavedState(Parcelable superState, @State int state) {
            super(superState);
            this.state = state;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }
    }

    private class SettleRunnable implements Runnable {

        private final View mView;

        @State
        private final int mTargetState;

        SettleRunnable(View view, @State int targetState) {
            mView = view;
            mTargetState = targetState;
        }

        @Override
        public void run() {
            if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(mView, this);
            } else {
                setStateInternal(mTargetState);
            }
        }
    }

}