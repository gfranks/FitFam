package com.github.gfranks.fitfam.adapter.holder;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.github.gfranks.fitfam.adapter.SettingsListAdapter;
import com.github.gfranks.fitfam.util.SettingsItem;
import com.github.gfranks.fitfam.util.Utils;
import com.github.gfranks.fitfam.view.FFRecyclerView;
import com.github.gfranks.fitfam.R;
import com.urbanairship.UAirship;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingsViewHolder extends FFRecyclerView.ViewHolder {

    @InjectView(R.id.settings_item_container)
    View mContainer;
    @InjectView(R.id.settings_item_text)
    TextView mText;
    @InjectView(R.id.settings_item_subtext)
    TextView mSubText;
    @InjectView(R.id.settings_item_switch)
    SwitchCompat mSwitch;
    @InjectView(R.id.settings_item_notification_quiet_time_container)
    View mNotificationQuietTimeContainer;
    @InjectView(R.id.settings_item_notification_quiet_time_start)
    TextView mNotificationQuietTimeStart;
    @InjectView(R.id.settings_item_notification_quiet_time_end)
    TextView mNotificationQuietTimeEnd;

    private SettingsListAdapter.OnSettingsItemClickListener mOnSettingsItemClickListener;

    public SettingsViewHolder(View v, SettingsListAdapter.OnSettingsItemClickListener listener) {
        super(v);
        ButterKnife.inject(this, v);
        mOnSettingsItemClickListener = listener;
    }

    public void populate(final SettingsItem item) {
        mText.setText(itemView.getContext().getResources().getStringArray(R.array.settings_items)[item.ordinal()]);
        mSubText.setText(itemView.getContext().getResources().getStringArray(R.array.settings_sub_items)[item.ordinal()]);
        if (item == SettingsItem.NOTIFICATIONS) {
            mSubText.setVisibility(View.VISIBLE);
            mSwitch.setVisibility(View.VISIBLE);
            mSwitch.setChecked(UAirship.shared().getPushManager().isPushEnabled());

            UAirship uAirship = UAirship.shared();
            DateTimeFormatter quiteTimeIntervalFormatter = Utils.getNotificationQuietTimeIntervalFormatter();
            Date[] quiteTimeInterval = uAirship.getPushManager().getQuietTimeInterval();
            mNotificationQuietTimeContainer.setVisibility(View.VISIBLE);
            mNotificationQuietTimeStart.setText(quiteTimeIntervalFormatter.print(new DateTime(quiteTimeInterval[0])));
            mNotificationQuietTimeEnd.setText(quiteTimeIntervalFormatter.print(new DateTime(quiteTimeInterval[1])));

            mNotificationQuietTimeStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSettingsItemClickListener != null) {
                        mOnSettingsItemClickListener.onChangeQuietTimeStart();
                    }
                }
            });

            mNotificationQuietTimeEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSettingsItemClickListener != null) {
                        mOnSettingsItemClickListener.onChangeQuietTimeEnd();
                    }
                }
            });
            itemView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            mSubText.setVisibility(View.GONE);
            mSwitch.setVisibility(View.GONE);
            mNotificationQuietTimeContainer.setVisibility(View.GONE);
            itemView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (SettingsItem.values()[getAdapterPosition()]) {
                    case NOTIFICATIONS:
                        UAirship.shared().getPushManager().setPushEnabled(isChecked);
                        mNotificationQuietTimeContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                        break;
                }
            }
        });

        if (mSwitch.getVisibility() == View.VISIBLE) {
            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwitch.setChecked(!mSwitch.isChecked());
                }
            });
        }
    }
}