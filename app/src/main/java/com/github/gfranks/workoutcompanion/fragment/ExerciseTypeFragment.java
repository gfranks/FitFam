package com.github.gfranks.workoutcompanion.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.adapter.ExerciseTypeAdapter;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.manager.AccountManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseTypeFragment extends BaseFragment implements Callback<List<String>> {

    public static final String TAG = "exercise_type_fragment";

    @Inject
    WorkoutCompanionService mService;
    @Inject
    AccountManager mAccountManager;

    @InjectView(R.id.exercise_request)
    Button mExerciseRequest;
    @InjectView(R.id.exercise_grid)
    RecyclerView mExerciseGrid;

    private WCUser mUser;
    private ExerciseTypeAdapter mAdapter;
    private boolean mEditMode;

    public static ExerciseTypeFragment newInstance(WCUser user) {
        ExerciseTypeFragment fragment = new ExerciseTypeFragment();
        Bundle args = new Bundle();
        args.putParcelable(WCUser.EXTRA, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mUser = savedInstanceState.getParcelable(WCUser.EXTRA);
        } else if (getArguments() != null) {
            mUser = getArguments().getParcelable(WCUser.EXTRA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_type, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((GridLayoutManager) mExerciseGrid.getLayoutManager()).setSpanCount(4);
        mExerciseGrid.setEnabled(false);
        mExerciseRequest.setEnabled(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(WCUser.EXTRA, mUser);
    }

    /**
     * **********************
     * Callback<List<String>>
     * **********************
     */
    @Override
    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        if (mAccountManager.getUser().equals(mUser) && !response.body().isEmpty()) {
            mExerciseRequest.setVisibility(View.VISIBLE);
        } else {
            mExerciseRequest.setVisibility(View.GONE);
        }
        if (mAdapter == null || mExerciseGrid.getAdapter() == null) {
            mAdapter = new ExerciseTypeAdapter(mUser, response.body(), mEditMode);
            mExerciseGrid.setAdapter(mAdapter);
        } else {
            mAdapter.setExercises(response.body());
        }
    }

    @Override
    public void onFailure(Call<List<String>> call, Throwable t) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        GFMinimalNotification.make(getView(), R.string.error_unable_to_load_exercises, GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_WARNING).show();
        if (mAdapter == null || mExerciseGrid.getAdapter() == null) {
            mAdapter = new ExerciseTypeAdapter(mUser, new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.exercises))), mEditMode);
            mExerciseGrid.setAdapter(mAdapter);
        } else {
            mAdapter.setExercises(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.exercises))));
        }
    }

    /**
     * ********************
     * View.OnClickListener
     * ********************
     */
    @OnClick(R.id.exercise_request)
    void onExerciseRequest() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.request_exercise)
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.request)
                .input(getString(R.string.exercise), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        mService.requestNewExercise(input.toString()).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                List<String> exercises = mAdapter.getExercises();
                                exercises.add(response.body());
                                mAdapter.setExercises(exercises);
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                if (isDetached() || getActivity() == null) {
                                    return;
                                }
                                GFMinimalNotification.make(getView(), t.getMessage(), GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_WARNING).show();
                            }
                        });
                    }
                }).show();
    }

    public void setUser(WCUser user) {
        mUser = user;
        if (!isDetached() && getActivity() != null) {
            mService.getExercises().enqueue(this);
        }
    }

    public List<String> getExercises() {
        return mUser.getExercises();
    }

    public void edit(boolean editMode) {
        mEditMode = editMode;
        mExerciseRequest.setEnabled(mEditMode);
        mExerciseGrid.setEnabled(mEditMode);
        mAdapter.setEnabled(mEditMode);
    }
}