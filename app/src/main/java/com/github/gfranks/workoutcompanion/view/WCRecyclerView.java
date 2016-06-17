package com.github.gfranks.workoutcompanion.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.util.DividerItemDecoration;

public class WCRecyclerView extends RecyclerView implements RecyclerView.OnItemTouchListener {

    private View mEmptyView;
    private RecyclerViewDataSetObserver mDataSetObserver;
    private OnItemClickListener mOnItemClickListener;
    private GestureDetector mGestureDetector;
    private boolean mDisableOnItemClick;

    public WCRecyclerView(Context context) {
        super(context);
        init(null, 0);
    }

    public WCRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WCRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mDataSetObserver);
        }
        mDataSetObserver.onChanged();
        scheduleLayoutAnimation();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (!mDisableOnItemClick) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mOnItemClickListener != null && mGestureDetector.onTouchEvent(e)) {
                int position = rv.getChildAdapterPosition(childView);
                mOnItemClickListener.onItemClick(this, rv.findViewHolderForAdapterPosition(position), position);
            }
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        updateEmptyStatus(getAdapter() == null || getAdapter().getItemCount() == 0);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void disableOnItemClick(boolean disableOnItemClick) {
        mDisableOnItemClick = disableOnItemClick;
    }

    private void init(AttributeSet attrs, int defStyle) {
        boolean layoutAnimation = false;
        boolean useDividers = false;
        int orientation = VERTICAL;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WCRecyclerView, defStyle, 0);
            layoutAnimation = a.getBoolean(R.styleable.WCRecyclerView_wcrv_layoutAnimation, false);
            useDividers = a.getBoolean(R.styleable.WCRecyclerView_wcrv_dividers, false);
            orientation = a.getInt(R.styleable.WCRecyclerView_wcrv_orientation, orientation);
            a.recycle();
        }
        if (useDividers) {
            addItemDecoration(new DividerItemDecoration(getContext(), orientation));
        }
        addOnItemTouchListener(this);
        mDataSetObserver = new RecyclerViewDataSetObserver();
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        if (layoutAnimation) {
            setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.recycler_view_layout_animation));
        }
    }

    private void updateEmptyStatus(boolean empty) {
        if (empty) {
            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.VISIBLE);
                setVisibility(View.GONE);
            } else {
                // If the caller just removed our empty view, make sure the recycler view is visible
                setVisibility(View.VISIBLE);
            }
        } else {
            if (mEmptyView != null) mEmptyView.setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, int position);
    }

    class RecyclerViewDataSetObserver extends AdapterDataObserver {

        @Override
        public void onChanged() {
            updateEmptyStatus(getAdapter() == null || getAdapter().getItemCount() == 0);
        }
    }
}
