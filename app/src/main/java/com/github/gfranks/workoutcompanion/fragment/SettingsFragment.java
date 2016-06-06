package com.github.gfranks.workoutcompanion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.BuildConfig;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.adapter.SettingsListAdapter;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.view.WCRecyclerView;
import com.urbanairship.UAirship;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

import butterknife.InjectView;

public class SettingsFragment extends BaseFragment implements WCRecyclerView.OnItemClickListener, SettingsListAdapter.OnSettingsItemClickListener {

    public static final String TAG = "settings_fragment";
    @InjectView(R.id.settings_list)
    WCRecyclerView mListView;
    @InjectView(R.id.settings_build_version)
    TextView mBuildVersion;
    private SettingsListAdapter mAdapter;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new SettingsListAdapter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
        mBuildVersion.setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.nav_settings);
    }

    /**
     * **********************************
     * WCRecyclerView.OnItemClickListener
     * **********************************
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder vh, int position) {

    }

    /**
     * ***********************************************
     * SettingsListAdapter.OnSettingsItemClickListener
     * ***********************************************
     */
    @Override
    public void onChangeQuietTimeStart() {
        DateTime start = new DateTime(UAirship.shared().getPushManager().getQuietTimeInterval()[0]);
        TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                UAirship uAirship = UAirship.shared();
                if (!uAirship.getPushManager().isQuietTimeEnabled()) {
                    uAirship.getPushManager().setQuietTimeEnabled(true);
                }

                uAirship.getPushManager().setQuietTimeInterval(
                        new LocalTime(hourOfDay, minute).toDateTimeToday(DateTimeZone.getDefault()).toDate(),
                        uAirship.getPushManager().getQuietTimeInterval()[1]);
                mListView.getAdapter().notifyDataSetChanged();
            }
        }, start.hourOfDay().get(), start.minuteOfHour().get(), false)
                .show(getActivity().getFragmentManager(), null);
    }

    @Override
    public void onChangeQuietTimeEnd() {
        DateTime end = new DateTime(UAirship.shared().getPushManager().getQuietTimeInterval()[1]);
        TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                UAirship uAirship = UAirship.shared();
                if (!uAirship.getPushManager().isQuietTimeEnabled()) {
                    uAirship.getPushManager().setQuietTimeEnabled(true);
                }

                uAirship.getPushManager().setQuietTimeInterval(
                        uAirship.getPushManager().getQuietTimeInterval()[0],
                        new LocalTime(hourOfDay, minute).toDateTimeToday(DateTimeZone.getDefault()).toDate());
                mListView.getAdapter().notifyDataSetChanged();
            }
        }, end.hourOfDay().get(), end.minuteOfHour().get(), false)
                .show(getActivity().getFragmentManager(), null);
    }
}
