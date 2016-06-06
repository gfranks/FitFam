package com.github.gfranks.workoutcompanion.data.api;

import com.github.gfranks.workoutcompanion.data.model.WCDiscoverResponse;
import com.github.gfranks.workoutcompanion.data.model.WCLocationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DiscoverService {

    @GET("/maps/api/place/search/json?radius=1000&types=workout,exercise&name=gym")
    Call<WCDiscoverResponse> getGyms(@Query("location") String location,
                                     @Query("key") String key);

    @GET("/maps/api/geocode/json")
    Call<WCLocationResponse> getLocations(@Query("address") String address,
                                          @Query("key") String key);
}
