package com.github.gfranks.fitfam.manager;

import android.content.SharedPreferences;

import com.github.gfranks.fitfam.data.model.FFCompanionFilters;
import com.github.gfranks.fitfam.data.model.FFGym;
import com.github.gfranks.fitfam.util.Utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import java.util.List;

import info.metadude.android.typedpreferences.StringPreference;

public class FilterManager {

    private static final String KEY_FILTER_OPTIONS = "filters";
    private static final String DEFAULT_FILTER_OPTIONS = "{}";

    private StringPreference mFilterOptionsStringPreference;

    public FilterManager(SharedPreferences prefs) {
        mFilterOptionsStringPreference = new StringPreference(prefs, KEY_FILTER_OPTIONS, DEFAULT_FILTER_OPTIONS);
    }

    public FFCompanionFilters getFilterOptions() {
            return Utils.getGson().fromJson(mFilterOptionsStringPreference.get(), FFCompanionFilters.class);
    }

    public void setFilterOptions(FFCompanionFilters filterOptions) {
        mFilterOptionsStringPreference.set(Utils.getGson().toJson(filterOptions));
    }

    public void setFilterByGym(FFGym gym) {
        FFCompanionFilters filterOptions = getFilterOptions();
        filterOptions.setGym(gym);
        setFilterOptions(filterOptions);
    }

    public void setFilterBySex(String sex) {
        FFCompanionFilters filterOptions = getFilterOptions();
        filterOptions.setSex(sex);
        setFilterOptions(filterOptions);
    }

    public void setFilterByAge(int age) {
        FFCompanionFilters filterOptions = getFilterOptions();
        filterOptions.setAge(age);
        setFilterOptions(filterOptions);
    }

    public void setFilterByAge(DateTime dateTime) {
        FFCompanionFilters filterOptions = getFilterOptions();
        filterOptions.setAge(new DateTime().get(DateTimeFieldType.year()) - dateTime.get(DateTimeFieldType.year()));
        setFilterOptions(filterOptions);
    }

    public void setFilterByWeight(int weight) {
        FFCompanionFilters filterOptions = getFilterOptions();
        filterOptions.setWeight(weight);
        setFilterOptions(filterOptions);
    }

    public void setFilterByExercises(List<String> exercises) {
        FFCompanionFilters filterOptions = getFilterOptions();
        filterOptions.setExercises(exercises);
        setFilterOptions(filterOptions);
    }
}
