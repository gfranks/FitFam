package com.github.gfranks.workoutcompanion.manager;

import android.content.SharedPreferences;

import com.github.gfranks.workoutcompanion.data.model.WCCompanionFilters;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.util.Utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import info.metadude.android.typedpreferences.StringPreference;

public class FilterManager {

    private static final String KEY_FILTER_OPTIONS = "filters";
    private static final String DEFAULT_FILTER_OPTIONS = "{}";

    private StringPreference mFilterOptionsStringPreference;

    public FilterManager(SharedPreferences prefs) {
        mFilterOptionsStringPreference = new StringPreference(prefs, KEY_FILTER_OPTIONS, DEFAULT_FILTER_OPTIONS);
    }

    public WCCompanionFilters getFilterOptions() {
            return Utils.getGson().fromJson(mFilterOptionsStringPreference.get(), WCCompanionFilters.class);
    }

    public void setFilterOptions(WCCompanionFilters filterOptions) {
        mFilterOptionsStringPreference.set(Utils.getGson().toJson(filterOptions));
    }

    public void setFilterByGym(WCGym gym) {
        WCCompanionFilters filterOptions = getFilterOptions();
        filterOptions.setGym(gym);
        setFilterOptions(filterOptions);
    }

    public void setFilterBySex(String sex) {
        WCCompanionFilters filterOptions = getFilterOptions();
        filterOptions.setSex(sex);
        setFilterOptions(filterOptions);
    }

    public void setFilterByAge(int age) {
        WCCompanionFilters filterOptions = getFilterOptions();
        filterOptions.setAge(age);
        setFilterOptions(filterOptions);
    }

    public void setFilterByAge(DateTime dateTime) {
        WCCompanionFilters filterOptions = getFilterOptions();
        filterOptions.setAge(new DateTime().get(DateTimeFieldType.year()) - dateTime.get(DateTimeFieldType.year()));
        setFilterOptions(filterOptions);
    }

    public void setFilterByWeight(int weight) {
        WCCompanionFilters filterOptions = getFilterOptions();
        filterOptions.setWeight(weight);
        setFilterOptions(filterOptions);
    }
}
