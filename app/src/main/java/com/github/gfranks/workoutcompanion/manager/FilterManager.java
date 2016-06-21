package com.github.gfranks.workoutcompanion.manager;

import android.content.SharedPreferences;

import com.github.gfranks.workoutcompanion.data.model.WCCompanionFilterOptions;
import com.github.gfranks.workoutcompanion.util.Utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import info.metadude.android.typedpreferences.StringPreference;

public class FilterManager {

    private static final String KEY_FILTER_OPTIONS = "user";
    private static final String DEFAULT_FILTER_OPTIONS = "{}";

    private StringPreference mFilterOptionsStringPreference;

    private WCCompanionFilterOptions mFilterOptions;

    public FilterManager(SharedPreferences prefs) {
        mFilterOptionsStringPreference = new StringPreference(prefs, KEY_FILTER_OPTIONS, DEFAULT_FILTER_OPTIONS);
    }

    public void setFilterOptions(WCCompanionFilterOptions filterOptions) {
        mFilterOptions = filterOptions;
        mFilterOptionsStringPreference.set(Utils.getGson().toJson(filterOptions));
    }

    public WCCompanionFilterOptions getFilterOptions() {
        if (mFilterOptions == null) {
            mFilterOptions = Utils.getGson().fromJson(mFilterOptionsStringPreference.get(), WCCompanionFilterOptions.class);
        }

        return mFilterOptions;
    }

    public void setFilterByGym(String gym) {
        WCCompanionFilterOptions filterOptions = getFilterOptions();
        filterOptions.setGymName(gym);
        setFilterOptions(filterOptions);
    }

    public void setFilterBySex(String sex) {
        WCCompanionFilterOptions filterOptions = getFilterOptions();
        filterOptions.setSex(sex);
        setFilterOptions(filterOptions);
    }

    public void setFilterByAge(int age) {
        WCCompanionFilterOptions filterOptions = getFilterOptions();
        filterOptions.setAge(age);
        setFilterOptions(filterOptions);
    }

    public void setFilterByAge(DateTime dateTime) {
        WCCompanionFilterOptions filterOptions = getFilterOptions();
        filterOptions.setAge(new DateTime().get(DateTimeFieldType.year()) - dateTime.get(DateTimeFieldType.year()));
        setFilterOptions(filterOptions);
    }

    public void setFilterByWeight(int weight) {
        WCCompanionFilterOptions filterOptions = getFilterOptions();
        filterOptions.setWeight(weight);
        setFilterOptions(filterOptions);
    }
}
