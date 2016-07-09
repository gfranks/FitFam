package com.github.gfranks.fitfam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * An implementation of {@link BaseAdapter} which uses the new/bind pattern for its views.
 */
public abstract class BindableAdapter<T> extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater mInflater;

    public BindableAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public BindableAdapter(Context context, int layoutResId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public abstract T getItem(int position);

    @Override
    public final View getView(int position, View view, ViewGroup container) {
        if (view == null) {
            view = newView(mInflater, position, container);
            if (view == null) {
                throw new IllegalStateException("newView result must not be null.");
            }
        }
        bindView(getItem(position), position, view);
        return view;
    }

    /**
     * Create a new instance of a view for the specified position.
     */
    public abstract View newView(LayoutInflater inflater, int position, ViewGroup container);

    /**
     * Bind the data for the specified {@code position} to the view.
     */
    public abstract void bindView(T item, int position, View view);

    @Override
    public final View getDropDownView(int position, View view, ViewGroup container) {
        if (view == null) {
            view = newDropDownView(mInflater, position, container);
            if (view == null) {
                throw new IllegalStateException("newDropDownView result must not be null.");
            }
        }
        bindDropDownView(getItem(position), position, view);
        return view;
    }

    /**
     * Create a new instance of a drop-down view for the specified position.
     */
    public View newDropDownView(LayoutInflater inflater, int position, ViewGroup container) {
        return newView(inflater, position, container);
    }

    /**
     * Bind the data for the specified {@code position} to the drop-down view.
     */
    public void bindDropDownView(T item, int position, View view) {
        bindView(item, position, view);
    }
}