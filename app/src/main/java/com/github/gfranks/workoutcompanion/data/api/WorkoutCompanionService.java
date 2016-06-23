package com.github.gfranks.workoutcompanion.data.api;

import com.github.gfranks.workoutcompanion.data.model.WCCompanionFilters;
import com.github.gfranks.workoutcompanion.data.model.WCUser;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface WorkoutCompanionService {

    /**
     * *******
     * Account
     * *******
     */
    @Multipart
    @POST("/v1/login")
    Call<WCUser> login(
            @Part("email") String email,
            @Part("password") String password);

    @Multipart
    @POST("/v1/accounts")
    Call<WCUser> createAccount(
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
    Call<WCUser> getUser(
            @Path("userId") String userId);

    @POST("/v1/users/{userId}")
    Call<WCUser> updateUser(
            @Path("userId") String userId,
            @Body WCUser user);

    @POST("/v1/users/{userId}/requestWorkout")
    Call<WCUser> requestWorkout(
            @Path("userId") String userId);

    @GET("/v1/users/{placeId}")
    Call<List<WCUser>> getUsers(
            @Path("placeId") String placeId,
            @Body WCCompanionFilters filters);

    @GET("/v1/users/{userId}/companions")
    Call<List<WCUser>> getRecentCompanions(
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
