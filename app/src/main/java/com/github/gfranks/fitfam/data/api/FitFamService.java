package com.github.gfranks.fitfam.data.api;

import com.github.gfranks.fitfam.data.model.FFCompanionFilters;
import com.github.gfranks.fitfam.data.model.FFUser;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface FitFamService {

    /**
     * *******
     * Account
     * *******
     */
    @Multipart
    @POST("/v1/login")
    Call<FFUser> login(
            @Part("email") String email,
            @Part("password") String password);

    @Multipart
    @POST("/v1/accounts")
    Call<FFUser> createAccount(
            @Part("email") String email,
            @Part("password") String password);

    @POST("/v1/accounts/{userId}/push")
    Call<ResponseBody> registerPush(
            @Path("userId") String userId,
            @Body String token);

    /**
     * *****
     * Users
     * *****
     */
    @GET("/v1/users/{userId}")
    Call<FFUser> getUser(
            @Path("userId") String userId);

    @POST("/v1/users/{userId}")
    Call<FFUser> updateUser(
            @Path("userId") String userId,
            @Body FFUser user);

    @POST("/v1/users/{userId}/requestWorkout")
    Call<FFUser> requestWorkout(
            @Path("userId") String userId);

    @GET("/v1/users/{placeId}")
    Call<List<FFUser>> getUsers(
            @Path("placeId") String placeId,
            @Body FFCompanionFilters filters);

    @GET("/v1/users/{userId}/companions")
    Call<List<FFUser>> getRecentCompanions(
            @Path("userId") String userId);

    /**
     * *********
     * Exercises
     * *********
     */
    @GET("/v1/exercises")
    Call<List<String>> getExercises();

    @POST("/v1/exercises/request")
    Call<String> requestNewExercise(
            @Body String exercise);
}
