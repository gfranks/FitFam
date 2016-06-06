package com.github.gfranks.workoutcompanion.data.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NullAwareCallback<T> implements Callback<T> {

    public static NullAwareCallback<ResponseBody> get() {
        return new NullAwareCallback<>();
    }


    @Override
    public void onResponse(Call<T> call, Response<T> response) {

    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {

    }
}
