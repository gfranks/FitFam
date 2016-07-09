package com.github.gfranks.fitfam.data.api;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.data.model.FFCompanionFilters;
import com.github.gfranks.fitfam.data.model.FFUser;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.util.UserDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Singleton;

import info.metadude.android.typedpreferences.StringPreference;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Part;
import retrofit2.http.Path;

@Singleton
public class MockFitFamService implements FitFamService {

    private AccountManager mAccountManager;
    private UserDatabase mUserDatabase;
    private StringPreference mExercisesPreference;

    public MockFitFamService(Application application, AccountManager accountManager, SharedPreferences prefs) {
        mAccountManager = accountManager;
        mUserDatabase = new UserDatabase(application);
        mExercisesPreference = new StringPreference(prefs, "exercises", TextUtils.join(",", application.getResources().getStringArray(R.array.exercises)));
        setupMockUsers();
    }

    /**
     * *******
     * Account
     * *******
     */
    @Override
    public Call<FFUser> login(@Part("email") final String email, @Part("password") final String password) {
        return new MockCall<FFUser>() {
            @Override
            public void enqueue(Callback<FFUser> cb) {
                try {
                    mUserDatabase.open();
                    final FFUser user = mUserDatabase.findUserByEmail(email);
                    if (user == null || !user.getPassword().equals(password)) {
                        cb.onFailure(this, new Throwable("You have entered the wrong email and/or password"));
                    } else {
                        cb.onResponse(this, Response.success(user));
                    }
                    mUserDatabase.close();
                } catch (Throwable t) {
                    cb.onFailure(this, new Throwable("You have entered the wrong email and/or password"));
                }
            }
        };
    }

    @Override
    public Call<FFUser> createAccount(@Part("email") final String email, @Part("password") final String password) {
        return new MockCall<FFUser>() {
            @Override
            public void enqueue(Callback<FFUser> cb) {
                FFUser user = new FFUser.Builder()
                        .setEmail(email)
                        .setPassword(password)
                        .setImage(getRandomImage())
                        .build();
                try {
                    mUserDatabase.open();
                    List<FFUser> users = mUserDatabase.getAllUsers();
                    for (FFUser existingUser : users) {
                        if (existingUser.getEmail().equals(email)) {
                            if (cb != null) {
                                cb.onFailure(this, new Throwable("A user already exists with this email"));
                            }
                            return;
                        }
                    }
                    mUserDatabase.createUser(user);
                    mUserDatabase.close();
                    if (cb != null) {
                        cb.onResponse(this, Response.success(user));
                    }
                } catch (Throwable t) {
                    if (cb != null) {
                        cb.onFailure(this, new Throwable("An error occurred when attempting to create account"));
                    }
                }
            }
        };
    }

    @Override
    public Call<ResponseBody> registerPush(@Path("userId") String userId, @Body String token) {
        return new MockCall<ResponseBody>() {
            @Override
            public void enqueue(Callback<ResponseBody> cb) {
                try {
                    cb.onResponse(this, Response.success(ResponseBody.create(null, "")));
                } catch (Throwable t) {
                    cb.onFailure(this, new Throwable("An error occurred"));
                }
            }
        };
    }

    /**
     * *****
     * Users
     * *****
     */
    @Override
    public Call<FFUser> getUser(@Path("userId") final String userId) {
        return new MockCall<FFUser>() {
            @Override
            public void enqueue(Callback<FFUser> cb) {
                try {
                    mUserDatabase.open();
                    FFUser user = mUserDatabase.findUserById(userId);
                    mUserDatabase.close();
                    if (cb != null) {
                        cb.onResponse(this, Response.success(user));
                    }
                } catch (Throwable t) {
                    if (cb != null) {
                        cb.onFailure(this, new Throwable("Unable to obtain user"));
                    }
                }
            }
        };
    }

    @Override
    public Call<FFUser> updateUser(@Path("userId") String userId, @Body final FFUser user) {
        return new MockCall<FFUser>() {
            @Override
            public void enqueue(Callback<FFUser> cb) {
                try {
                    mUserDatabase.open();
                    mUserDatabase.updateUser(user);
                    mUserDatabase.close();
                    if (cb != null) {
                        cb.onResponse(this, Response.success(user));
                    }
                } catch (Throwable t) {
                    if (cb != null) {
                        cb.onFailure(this, new Throwable("An error occurred when attempting to update user"));
                    }
                }
            }
        };
    }

    @Override
    public Call<FFUser> requestWorkout(@Path("userId") final String userId) {
        return new MockCall<FFUser>() {
            @Override
            public void enqueue(Callback<FFUser> cb) {
                try {
                    mUserDatabase.open();
                    FFUser user = mUserDatabase.findUserById(userId);
                    user.setCanSeeContactInfo(true);
                    mUserDatabase.updateUser(user);
                    mUserDatabase.close();
                    if (cb != null) {
                        cb.onResponse(this, Response.success(user));
                    }
                } catch (Throwable t) {
                    if (cb != null) {
                        cb.onFailure(this, new Throwable("An error occurred when attempting to update user"));
                    }
                }
            }
        };
    }

    @Override
    public Call<List<FFUser>> getUsers(@Path("placeId") final String placeId, @Body final FFCompanionFilters filters) {
        return new MockCall<List<FFUser>>() {
            @Override
            public void enqueue(Callback<List<FFUser>> cb) {
                List<FFUser> users;
                try {
                    mUserDatabase.open();
                    users = mUserDatabase.getAllUsers();
                    for (FFUser user : new ArrayList<>(users)) {
                        if (user.equals(mAccountManager.getUser()) || (!placeId.equals(user.getHomeGymId())
                                && !user.getGymIds().contains(placeId))) {
                            users.remove(user);
                        }
                    }

                    mUserDatabase.close();

                    users = filterUsers(users, filters);
                    if (cb != null) {
                        cb.onResponse(this, Response.success(users));
                    }
                } catch (Throwable t) {
                    if (cb != null) {
                        cb.onFailure(this, new Throwable("Unable to retrieve users"));
                    }
                }
            }
        };
    }

    @Override
    public Call<List<FFUser>> getRecentCompanions(@Path("userId") String userId) {
        return new MockCall<List<FFUser>>() {
            @Override
            public void enqueue(Callback<List<FFUser>> cb) {
                List<FFUser> users;
                try {
                    mUserDatabase.open();
                    users = mUserDatabase.getAllUsers();
                    for (FFUser user : new ArrayList<>(users)) {
                        if (user.equals(mAccountManager.getUser()) || !user.isCanSeeContactInfo()) {
                            users.remove(user);
                        }
                    }

                    mUserDatabase.close();
                    if (cb != null) {
                        cb.onResponse(this, Response.success(users));
                    }
                } catch (Throwable t) {
                    if (cb != null) {
                        cb.onFailure(this, new Throwable("Unable to retrieve users"));
                    }
                }
            }
        };
    }

    /**
     * *********
     * Exercises
     * *********
     */
    @Override
    public Call<List<String>> getExercises() {
        return new MockCall<List<String>>() {
            @Override
            public void enqueue(Callback<List<String>> cb) {
                if (cb != null) {
                    List<String> exercises = new ArrayList<>(Arrays.asList(mExercisesPreference.get().split(",")));
                    cb.onResponse(this, Response.success(exercises));
                }
            }
        };
    }

    @Override
    public Call<String> requestNewExercise(@Body final String exercise) {
        return new MockCall<String>() {
            @Override
            public void enqueue(Callback<String> cb) {
                if (cb != null) {
                    List<String> exercises = new ArrayList<>(Arrays.asList(mExercisesPreference.get().split(",")));
                    exercises.add(exercise);
                    mExercisesPreference.set(TextUtils.join(",", exercises));
                    cb.onResponse(this, Response.success(exercise));
                }
            }
        };
    }

    private void setupMockUsers() {
        try {
            mUserDatabase.open();
            mUserDatabase.createUser(new FFUser.Builder()
                    .setFirstName("Garrett")
                    .setLastName("Franks")
                    .setEmail("lgfz71@gmail.com")
                    .setPassword("password")
                    .setImage(getRandomImage())
                    .build());
            mUserDatabase.createUser(new FFUser.Builder()
                    .setFirstName("Mark")
                    .setLastName("Davis")
                    .setEmail("mark.davis@gmail.com")
                    .setPassword("password")
                    .setImage(getRandomImage())
                    .build());
            mUserDatabase.createUser(new FFUser.Builder()
                    .setFirstName("Jared")
                    .setLastName("Piatt")
                    .setEmail("jaredpiatt@gmail.com")
                    .setPassword("password")
                    .setImage(getRandomImage())
                    .build());
            mUserDatabase.createUser(new FFUser.Builder()
                    .setFirstName("James")
                    .setLastName("Finnigin")
                    .setEmail("jfinnigin@gmail.com")
                    .setPassword("password")
                    .setImage(getRandomImage())
                    .build());
            mUserDatabase.close();
        } catch (Throwable t) {
            // unable to create mock users
        }
    }

    private List<FFUser> filterUsers(List<FFUser> users, FFCompanionFilters filters) {
        if (filters.getLocation() == null && filters.getGym() == null && (filters.getSex() == null || filters.getSex().length() == 0)
                && filters.getAge() == 0 && filters.getWeight() == 0 && (filters.getExercises() == null || filters.getExercises().size() == 0)) {
            return users;
        }

        List<FFUser> filteredUsers = new ArrayList<>();
        for (FFUser user : users) {
            boolean matched = false;
            if (filters.getAge() != 0 && Math.abs(user.getBirthdayYear() - filters.getAge()) < 5) {
                matched = true;
            }

            if (filters.getWeight() != 0 && Math.abs(user.getWeight() - filters.getWeight()) < 15) {
                matched = true;
            }

            if (filters.getExercises() != null) {
                for (String exercise : filters.getExercises()) {
                    if (user.getExercises().contains(exercise)) {
                        matched = true;
                    }
                }
            }

            if (filters.getSex() != null) {
                if (user.getSex().equals(filters.getSex())) {
                    matched = true;
                }
            }

            if (filters.getGym() != null) {
                if (filters.getGym().getPlace_id().equals(user.getHomeGymId())) {
                    matched = true;
                }

                if (user.getGymIds() != null && user.getGymIds().contains(filters.getGym().getPlace_id())) {
                    matched = true;
                }
            }

            if (matched) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    private String getRandomImage() {
        String[] images = new String[]{
                "http://ia.media-imdb.com/images/M/MV5BOTk2NDc2ODgzMF5BMl5BanBnXkFtZTcwMTMzOTQ4Nw@@._V1_UX214_CR0,0,214,317_AL_.jpg",
                "http://cdn3-www.superherohype.com/assets/uploads/gallery/captain_america_4979/captain_america_the_winter_soldier_7927/captws_captainamerica_avatar.jpg",
                "http://img12.deviantart.net/3619/i/2013/096/e/7/iron_man_wallpaper_by_ktoll-d60ofvz.jpg",
                "http://www.themarysue.com/wp-content/uploads/2014/08/scarjo.jpg",
                "http://screenrant.com/wp-content/uploads/avengers-age-ultron-hulk-mark-ruffalo.jpg"
        };

        return images[new Random().nextInt(images.length)];
    }

    private abstract class MockCall<T> implements Call<T> {

        @Override
        public Response<T> execute() throws IOException {
            return null;
        }

        @Override
        public boolean isExecuted() {
            return true;
        }

        @Override
        public void cancel() {
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public Call<T> clone() {
            return null;
        }

        @Override
        public Request request() {
            return null;
        }
    }
}
