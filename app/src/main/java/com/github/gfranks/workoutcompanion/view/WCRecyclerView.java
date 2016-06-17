package com.github.gfranks.workoutcompanion.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.util.DividerItemDecoration;

public class WCRecyclerView extends RecyclerView {

    private View mEmptyView;
    private RecyclerViewDataSetObserver mDataSetObserver;
    private OnItemClickListener mOnItemClickListener;
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
        mDataSetObserver = new RecyclerViewDataSetObserver();

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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemView.getParent() != null && itemView.getParent() instanceof WCRecyclerView) {
                        WCRecyclerView rv = (WCRecyclerView) itemView.getParent();
                        if (rv.mOnItemClickListener != null && !rv.mDisableOnItemClick) {
                            rv.mOnItemClickListener.onItemClick(rv, rv.findViewHolderForAdapterPosition(getAdapterPosition()),
                                    getAdapterPosition());
                        }
                    }
                }
            });
        }
    }

    class RecyclerViewDataSetObserver extends AdapterDataObserver {

        @Override
        public void onChanged() {
            updateEmptyStatus(getAdapter() == null || getAdapter().getItemCount() == 0);
        }
    }
}
