package com.github.gfranks.fitfam.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.util.DividerItemDecoration;

public class FFRecyclerView extends RecyclerView {

    private View mEmptyView;
    private RecyclerViewDataSetObserver mDataSetObserver;
    private OnItemClickListener mOnItemClickListener;
    private boolean mDisableOnItemClick;
    private DividerItemDecoration mDividerItemDecoration;
    private int mOrientation = VERTICAL;

    public FFRecyclerView(Context context) {
        super(context);
        init(null, 0);
    }

    public FFRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FFRecyclerView(Context context, AttributeSet attrs, int defStyle) {
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

    public void setUseDividers(boolean useDividers) {
        if (mDividerItemDecoration != null) {
            removeItemDecoration(mDividerItemDecoration);
        }

        if (useDividers) {
            if (mDividerItemDecoration == null) {
                mDividerItemDecoration = new DividerItemDecoration(getContext(), mOrientation);
            }
            addItemDecoration(mDividerItemDecoration);
        }
    }

    private void init(AttributeSet attrs, int defStyle) {
        boolean layoutAnimation = false;
        boolean useDividers = false;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FFRecyclerView, defStyle, 0);
            layoutAnimation = a.getBoolean(R.styleable.FFRecyclerView_ffrv_layoutAnimation, false);
            useDividers = a.getBoolean(R.styleable.FFRecyclerView_ffrv_dividers, false);
            mOrientation = a.getInt(R.styleable.FFRecyclerView_ffrv_orientation, mOrientation);
            a.recycle();
        }
        if (useDividers) {
            mDividerItemDecoration = new DividerItemDecoration(getContext(), mOrientation);
            addItemDecoration(mDividerItemDecoration);
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
                    if (itemView.getParent() != null && itemView.getParent() instanceof FFRecyclerView) {
                        FFRecyclerView rv = (FFRecyclerView) itemView.getParent();
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
