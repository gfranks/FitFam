package com.github.gfranks.workoutcompanion.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.github.gfranks.workoutcompanion.BuildConfig;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.data.adapter.DateTimeAdapter;
import com.github.gfranks.workoutcompanion.data.adapter.LocalDateAdapter;
import com.github.gfranks.workoutcompanion.data.adapter.LocalTimeAdapter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.TimeZone;
import java.util.regex.Pattern;

public class Utils {

    private static final String PHONE_NUMBER_PATTERN = "^\\([0-9]{3}\\)\\s[0-9]{3}\\-[0-9]{4}$";
    private static final String NON_DIGIT_PATTERN = "[\\D]";
    private static final String BIRTHDAY_PATTERN = "^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4}$";

    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormat.forPattern(BuildConfig.DATE_TIME_PATTERN);
    }

    public static DateTimeFormatter getNotificationQuietTimeIntervalFormatter() {
        return DateTimeFormat.forPattern(BuildConfig.NOTIFICATION_QUIET_TIME_INTERVAL_PATTERN);
    }

    public static long getUtcTimeOffset() {
        return TimeZone.getDefault().getOffset(DateTime.now().getMillis());
    }

    public static String getUtcTimeOffsetInSeconds() {
        return String.valueOf(getUtcTimeOffset() / 1000);
    }

    public static String getUtcTimeOffsetInMinutes() {
        return String.valueOf(getUtcTimeOffset() / 1000 / 60);
    }

    public static String getUtcTimeOffsetInHours() {
        return String.valueOf(getUtcTimeOffset() / 1000 / 60 / 60);
    }

    public static void applyMenuTintColor(Context context, Menu menu) {
        if (context == null || context.getResources() == null) {
            return;
        }
        applyMenuTintColor(menu, ContextCompat.getColor(context, R.color.theme_default_text_light), ContextCompat.getColor(context, R.color.theme_default_text));
    }

    public static void applyMenuTintColor(Menu menu, int color, int subMenuColor) {
        if (menu != null) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                Drawable icon = item.getIcon();
                if (icon != null) {
                    icon = applyDrawableTint(icon, color, !item.isEnabled());
                    item.setIcon(icon);
                }

                View actionView = item.getActionView();
                if (actionView != null) {
                    if (actionView instanceof TextView) {
                        Drawable[] drawables = ((TextView) actionView).getCompoundDrawables();
                        for (int j = 0; j < drawables.length; j++) {
                            Drawable drawable = drawables[j];
                            if (drawable != null) {
                                drawable = applyDrawableTint(drawable, color, !item.isEnabled());
                            }
                        }
                        ((TextView) actionView).setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
                    } else if (actionView instanceof SearchView) {
                        try {
                            SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) actionView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                            searchAutoComplete.setHintTextColor(ContextCompat.getColor(actionView.getContext(), R.color.gray_super_light));
                        } catch (Throwable t) {
                            t.printStackTrace();
                            // unable to change searchview hint text color
                        }
                    }
                }
                applyMenuTintColor(item.getSubMenu(), subMenuColor, 0);
            }
        }
    }

    public static Drawable getTabIcon(Context context, int iconResId) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
                applyDrawableTint(context, iconResId, ContextCompat.getColor(context, R.color.theme_tab_bar_icon_color_selected)));
        stateListDrawable.addState(new int[]{android.R.attr.state_selected},
                applyDrawableTint(context, iconResId, ContextCompat.getColor(context, R.color.theme_tab_bar_icon_color_selected)));
        stateListDrawable.addState(new int[]{},
                applyDrawableTint(context, iconResId, ContextCompat.getColor(context, R.color.theme_tab_bar_icon_color)));
        return stateListDrawable;
    }

    public static Drawable applyWhiteDrawableTint(Context context, int drawableResId) {
        return applyWhiteDrawableTint(context, drawableResId, false);
    }

    public static Drawable applyWhiteDrawableTint(Context context, Drawable drawable) {
        return applyWhiteDrawableTint(context, drawable, false);
    }

    public static Drawable applyWhiteDrawableTint(Context context, int drawableResId, boolean isTransparent) {
        return applyDrawableTint(context, drawableResId, ContextCompat.getColor(context, R.color.white), isTransparent ? 77 : 255);
    }

    public static Drawable applyWhiteDrawableTint(Context context, Drawable drawable, boolean isTransparent) {
        return applyDrawableTint(drawable, ContextCompat.getColor(context, R.color.white), isTransparent ? 77 : 255);
    }

    public static Drawable applyDrawableTint(Drawable drawable, int color, boolean isTransparent) {
        return applyDrawableTint(drawable, color, isTransparent ? 77 : 255);
    }

    public static Drawable applyDrawableTint(Context context, int drawable, int color) {
        return applyDrawableTint(context, drawable, color, 255);
    }

    public static Drawable applyDrawableTint(Context context, int drawable, int color, int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return applyDrawableTint(ContextCompat.getDrawable(context, drawable), color, alpha);
        } else {
            return applyDrawableTint(ContextCompat.getDrawable(context, drawable), color, alpha);
        }
    }

    public static Drawable applyDrawableTint(Drawable drawable, int color) {
        return applyDrawableTint(drawable, color, 255);
    }

    public static Drawable applyDrawableTint(Drawable drawable, int color, int alpha) {
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            drawable.setAlpha(alpha);
        }

        return drawable;
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat(BuildConfig.DATE_TIME_PATTERN)
                .setPrettyPrinting()
                .enableComplexMapKeySerialization()
                .create();
    }

    public static boolean isPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.length() > 0
                && Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber);
    }

    public static String removeNonDigitValuesFromPhoneNumber(String phoneNumber, boolean format) {
        if (format) {
            if (isPhoneNumber(phoneNumber)) {
                return phoneNumber;
            }
            return formatPhoneNumber(phoneNumber.replaceAll(NON_DIGIT_PATTERN, ""));
        }

        return phoneNumber.replaceAll(NON_DIGIT_PATTERN, "");
    }

    public static String formatPhoneNumber(String phoneNumber) {
        // TODO: format for international numbers too
        String formattedPhoneNumber;
        if (phoneNumber.length() == 11) {
            formattedPhoneNumber = phoneNumber.substring(0, 1) + " (" + phoneNumber.substring(1, 4) + ") "
                    + phoneNumber.substring(4, 7) + "-" + phoneNumber.substring(7);
        } else {
            formattedPhoneNumber = "(" + phoneNumber.substring(0, 3) + ") "
                    + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6);
        }

        return formattedPhoneNumber;
    }

    public static boolean isBirthday(String birthday) {
        return birthday != null && birthday.length() > 0
                && Pattern.matches(BIRTHDAY_PATTERN, birthday);
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
