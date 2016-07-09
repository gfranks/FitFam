package com.github.gfranks.fitfam.data.api;

import com.github.gfranks.fitfam.data.model.FFGyms;
import com.github.gfranks.fitfam.data.model.FFLocations;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiService {

    @GET("/maps/api/place/search/json?radius=1500&types=workout,exercise&name=gym")
    Call<FFGyms> getGyms(@Query("location") String location,
                         @Query("key") String key);

    @GET("/maps/api/place/details/json")
    Call<FFGyms> getGymDetails(@Query("placeid") String placeId,
                               @Query("key") String key);

    @GET("/maps/api/geocode/json?components=country:US")
    Call<FFLocations> getLocations(@Query("address") String address,
                                   @Query("key") String key);
}
