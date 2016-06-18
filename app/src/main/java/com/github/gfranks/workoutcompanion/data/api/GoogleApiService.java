package com.github.gfranks.workoutcompanion.data.api;

import com.github.gfranks.workoutcompanion.data.model.WCGyms;
import com.github.gfranks.workoutcompanion.data.model.WCLocations;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiService {

    @GET("/maps/api/place/search/json?radius=1000&types=workout,exercise&name=gym")
    Call<WCGyms> getGyms(@Query("location") String location,
                         @Query("key") String key);

    @GET("/maps/api/place/details/json")
    Call<WCGyms> getGymDetails(@Query("placeid") String placeId,
                               @Query("key") String key);

    @GET("/maps/api/geocode/json?components=country:US")
    Call<WCLocations> getLocations(@Query("address") String address,
                                   @Query("key") String key);
}
