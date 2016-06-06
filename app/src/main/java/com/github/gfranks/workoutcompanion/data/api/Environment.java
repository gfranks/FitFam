package com.github.gfranks.workoutcompanion.data.api;

import com.github.gfranks.workoutcompanion.BuildConfig;

public enum Environment {
    PRODUCTION(BuildConfig.API_URL_PROD),
    QA(BuildConfig.API_URL_QA),
    CI(BuildConfig.API_URL_CI),
    // available in DEBUG only
    MOCK("http://mock");

    public static final String ENVIRONMENT = "environment";

    private final String url;

    Environment(String url) {
        this.url = url;
    }

    public static Environment from(String endpoint) {
        for (int i = 0; i < values().length; i++) {
            Environment value = values()[i];
            if (value.url != null && value.url.equals(endpoint)) {
                return value;
            }
        }
        return getDefault();
    }

    public static Environment getDefault() {
        if (BuildConfig.DEBUG) {
            if (BuildConfig.DEFAULT_MOCK_MODE) {
                return Environment.MOCK;
            } else {
                return Environment.QA;
            }
        } else {
            return Environment.PRODUCTION;
        }
    }

    public String url() {
        return url;
    }

    public boolean isMockMode() {
        return this == MOCK;
    }
}
