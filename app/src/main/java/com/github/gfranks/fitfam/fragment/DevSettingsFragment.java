package com.github.gfranks.fitfam.fragment;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.github.gfranks.fitfam.adapter.EnumAdapter;
import com.github.gfranks.fitfam.data.api.Environment;
import com.github.gfranks.fitfam.fragment.base.BaseFragment;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.util.Feature;
import com.github.gfranks.fitfam.BuildConfig;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.util.EndSheetBehavior;
import com.github.gfranks.fitfam.util.Utils;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.inject.Inject;

import butterknife.InjectView;
import okhttp3.logging.HttpLoggingInterceptor;

public class DevSettingsFragment extends BaseFragment {

    public static final String TAG = "dev_settings";

    @Inject
    SharedPreferences mPrefs;

    @Inject
    Picasso mPicasso;

    @Inject
    HttpLoggingInterceptor mLoggingInterceptor;

    @Inject
    AccountManager mAccountManager;

    @Inject
    Environment mDefaultEnvironment;

    @InjectView(R.id.dev_toggle)
    View mToggle;
    @InjectView(R.id.dev_build_name)
    TextView mBuildNameView;
    @InjectView(R.id.dev_build_code)
    TextView mBuildCodeView;
    @InjectView(R.id.dev_build_sha)
    TextView mBuildShaView;
    @InjectView(R.id.dev_build_date)
    TextView mBuildDateView;
    @InjectView(R.id.dev_device_make)
    TextView mDeviceMakeView;
    @InjectView(R.id.dev_device_model)
    TextView mDeviceModelView;
    @InjectView(R.id.dev_device_resolution)
    TextView mDeviceResolutionView;
    @InjectView(R.id.dev_device_density)
    TextView mDeviceDensityView;
    @InjectView(R.id.dev_device_release)
    TextView mDeviceReleaseView;
    @InjectView(R.id.dev_device_api)
    TextView mDeviceApiView;
    @InjectView(R.id.dev_network_logging)
    AppCompatSpinner mNetworkLoggingView;
    @InjectView(R.id.dev_images_logging)
    AppCompatSpinner mImagesLoggingView;
    @InjectView(R.id.dev_images_indicators)
    AppCompatSpinner mImagesIndicatorsView;
    @InjectView(R.id.dev_network_environment)
    AppCompatSpinner mNetworkEnvironment;
    @InjectView(R.id.dev_crashlytics)
    CheckBox mCrashlytics;

    private static String getDensityString(DisplayMetrics displayMetrics) {
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tvdpi";
            default:
                return "unknown";
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dev_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndSheetBehavior behavior = EndSheetBehavior.from((View) v.getParent());
                if (behavior.getState() == EndSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(EndSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(EndSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        setupFeaturesSection();
        setupBuildSection();
        setupDeviceSection();
        setupNetworkSection();
        setupImagesSection();
    }

    private void setupFeaturesSection() {
        mCrashlytics.setChecked(Feature.CRASHLYTICS.isEnabled(getContext()));
        mCrashlytics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Feature.CRASHLYTICS.setEnabled(getContext(), isChecked);
                System.exit(0);
            }
        });
    }

    private void setupNetworkSection() {
        // We use the JSON rest adapter as the source of truth for the log level.
        final EnumAdapter<HttpLoggingInterceptor.Level> loggingAdapter = new EnumAdapter<>(getContext(), HttpLoggingInterceptor.Level.class, R.layout.layout_dev_settings_spinner_item);
        mNetworkLoggingView.setAdapter(loggingAdapter);
        mNetworkLoggingView.setSelection(mLoggingInterceptor.getLevel().ordinal());
        mNetworkLoggingView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                HttpLoggingInterceptor.Level selected = loggingAdapter.getItem(position);
                if (selected != mLoggingInterceptor.getLevel()) {
                    mLoggingInterceptor.setLevel(selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final EnumAdapter<Environment> envAdapter = new EnumAdapter<Environment>(getContext(), Environment.class, R.layout.layout_dev_settings_spinner_item) {
            @Override
            public int getCount() {
                if (BuildConfig.DEBUG) {
                    return Environment.values().length;
                }
                // do not show mock
                return Environment.values().length - 1;
            }
        };
        mNetworkEnvironment.setAdapter(envAdapter);
        mNetworkEnvironment.setSelection(mDefaultEnvironment.ordinal());
        mNetworkEnvironment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (mPrefs.getInt(Environment.ENVIRONMENT, mDefaultEnvironment.ordinal()) != position) {
                    mPrefs.edit().putInt(Environment.ENVIRONMENT, position).apply();
                    mAccountManager.logout();
                    System.exit(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setupImagesSection() {
        final EnumAdapter<YesNoEnum> loggingAdapter = new EnumAdapter<>(getContext(), YesNoEnum.class, R.layout.layout_dev_settings_spinner_item);
        mImagesLoggingView.setAdapter(loggingAdapter);
        mImagesLoggingView.setSelection(mPicasso.isLoggingEnabled() ? 1 : 0);
        mImagesLoggingView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (mPicasso.isLoggingEnabled() && position == 1)
                    return;
                mPicasso.setLoggingEnabled(position == 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final EnumAdapter<YesNoEnum> indicatorsAdapter = new EnumAdapter<>(getContext(), YesNoEnum.class, R.layout.layout_dev_settings_spinner_item);
        mImagesIndicatorsView.setAdapter(indicatorsAdapter);
        mImagesIndicatorsView.setSelection(mPicasso.areIndicatorsEnabled() ? 1 : 0);
        mImagesIndicatorsView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (mPicasso.areIndicatorsEnabled() && position == 1)
                    return;
                mPicasso.setIndicatorsEnabled(position == 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setupBuildSection() {
        mBuildNameView.setText(BuildConfig.VERSION_NAME);
        mBuildCodeView.setText(String.valueOf(BuildConfig.VERSION_CODE));
        mBuildShaView.setText(BuildConfig.GIT_SHA);

        DateTime buildTime = DateTime.parse(BuildConfig.BUILD_TIME).toDateTime(DateTimeZone.getDefault());
        mBuildDateView.setText(Utils.getDateTimeFormatter().print(buildTime));
    }

    private void setupDeviceSection() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        String densityBucket = getDensityString(displayMetrics);
        mDeviceMakeView.setText(truncateAt(Build.MANUFACTURER, 20));
        mDeviceModelView.setText(truncateAt(Build.MODEL, 20));
        String res = displayMetrics.heightPixels + "x" + displayMetrics.widthPixels;
        mDeviceResolutionView.setText(res);
        String density = displayMetrics.densityDpi + "dpi (" + densityBucket + ")";
        mDeviceDensityView.setText(density);
        mDeviceReleaseView.setText(Build.VERSION.RELEASE);
        mDeviceApiView.setText(String.valueOf(Build.VERSION.SDK_INT));
    }

    private String truncateAt(String string, int length) {
        if (string != null && string.trim().length() > 0) {
            return string.length() > length ? string.substring(0, length) : string;
        } else {
            return string;
        }
    }

    private enum YesNoEnum {
        NO,
        YES
    }
}
