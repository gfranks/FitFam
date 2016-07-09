package com.github.gfranks.fitfam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EnumAdapter<T extends Enum<T>> extends BindableAdapter<T> {

    private final T[] mEnumConstants;
    private final boolean mShowNull;
    private final int mNullOffset;
    private int mLayoutResId = -1;

    public EnumAdapter(Context context, Class<T> enumType) {
        this(context, enumType, false);
    }

    public EnumAdapter(Context context, Class<T> enumType, int layoutResId) {
        this(context, enumType, false, layoutResId);
    }

    public EnumAdapter(Context context, Class<T> enumType, boolean mShowNull) {
        super(context);
        this.mEnumConstants = enumType.getEnumConstants();
        this.mShowNull = mShowNull;
        this.mNullOffset = mShowNull ? 1 : 0;
    }

    public EnumAdapter(Context context, Class<T> enumType, boolean mShowNull, int layoutResId) {
        super(context);
        this.mLayoutResId = layoutResId;
        this.mEnumConstants = enumType.getEnumConstants();
        this.mShowNull = mShowNull;
        this.mNullOffset = mShowNull ? 1 : 0;
    }

    @Override
    public int getCount() {
        return mEnumConstants.length + mNullOffset;
    }

    @Override
    public final T getItem(int position) {
        if (mShowNull && position == 0) {
            return null;
        }

        return mEnumConstants[position - mNullOffset];
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        if (mLayoutResId != -1) {
            return inflater.inflate(mLayoutResId, container, false);
        }
        return inflater.inflate(android.R.layout.simple_spinner_item, container, false);
    }

    @Override
    public void bindView(T item, int position, View view) {
        TextView tv = (TextView) view.findViewById(android.R.id.text1);
        tv.setText(getName(item));
    }

    @Override
    public View newDropDownView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(android.R.layout.simple_spinner_dropdown_item, container, false);
    }

    protected String getName(T item) {
        return String.valueOf(item);
    }
}
