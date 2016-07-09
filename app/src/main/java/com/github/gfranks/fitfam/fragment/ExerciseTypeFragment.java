package com.github.gfranks.fitfam.fragment;

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
import com.github.gfranks.fitfam.data.api.FitFamService;
import com.github.gfranks.fitfam.fragment.base.BaseFragment;
import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.adapter.ExerciseTypeAdapter;

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
    private static final String EXTRA_EXERCISES = "exercises";
    private static final String EXTRA_REQUEST_EXERCISES = "request_exercises";

    @Inject
    FitFamService mService;

    @InjectView(R.id.exercise_request)
    Button mExerciseRequest;
    @InjectView(R.id.exercise_grid)
    RecyclerView mExerciseGrid;

    private List<String> mExercises;
    private ExerciseTypeAdapter mAdapter;
    private boolean mCanRequestExercises, mEditMode;
    private OnExercisesChangedListener mOnExercisesChangedListener;

    public static ExerciseTypeFragment newInstance(List<String> exercises, boolean isEditMode, boolean canRequestExercises,
                                                   OnExercisesChangedListener onExercisesChangedListener) {
        ExerciseTypeFragment fragment = new ExerciseTypeFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(EXTRA_EXERCISES, new ArrayList<>(exercises));
        args.putBoolean(EXTRA_REQUEST_EXERCISES, canRequestExercises);
        fragment.setArguments(args);
        fragment.mEditMode = isEditMode;
        fragment.setOnExercisesChangedListener(onExercisesChangedListener);
        return fragment;
    }

    public static ExerciseTypeFragment newInstance(List<String> exercises, boolean isEditMode, boolean canRequestExercises) {
        return newInstance(exercises, isEditMode, canRequestExercises, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mExercises = savedInstanceState.getStringArrayList(EXTRA_EXERCISES);
            mCanRequestExercises = savedInstanceState.getBoolean(EXTRA_REQUEST_EXERCISES, false);
        } else if (getArguments() != null) {
            mExercises = getArguments().getStringArrayList(EXTRA_EXERCISES);
            mCanRequestExercises = getArguments().getBoolean(EXTRA_REQUEST_EXERCISES, false);
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
        mExerciseGrid.setEnabled(mEditMode);
        mExerciseRequest.setEnabled(mEditMode);

        initWithExercises();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(EXTRA_EXERCISES, new ArrayList<>(mExercises));
        outState.putBoolean(EXTRA_REQUEST_EXERCISES, mCanRequestExercises);
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
        if (mCanRequestExercises && !response.body().isEmpty()) {
            mExerciseRequest.setVisibility(View.VISIBLE);
        } else {
            mExerciseRequest.setVisibility(View.GONE);
        }
        if (mAdapter == null || mExerciseGrid.getAdapter() == null) {
            mAdapter = new ExerciseTypeAdapter(response.body(), new ArrayList<>(mExercises), mEditMode, mOnExercisesChangedListener);
            mExerciseGrid.setAdapter(mAdapter);
        } else {
            mAdapter.setExercises(response.body(), new ArrayList<>(mExercises));
        }
    }

    @Override
    public void onFailure(Call<List<String>> call, Throwable t) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        GFMinimalNotification.make(getView(), R.string.error_unable_to_load_exercises, GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_WARNING).show();
        if (mAdapter == null || mExerciseGrid.getAdapter() == null) {
            mAdapter = new ExerciseTypeAdapter(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.exercises))),
                    new ArrayList<>(mExercises), mEditMode, mOnExercisesChangedListener);
            mExerciseGrid.setAdapter(mAdapter);
        } else {
            mAdapter.setExercises(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.exercises))),
                    new ArrayList<>(mExercises));
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
                                mAdapter.setExercises(exercises, new ArrayList<>(mExercises));
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

    public void setOnExercisesChangedListener(OnExercisesChangedListener onExercisesChangedListener) {
        mOnExercisesChangedListener = onExercisesChangedListener;
    }

    public void setExercises(List<String> exercises, boolean canRequestExercises) {
        mExercises = exercises;
        mCanRequestExercises = canRequestExercises;
        initWithExercises();
    }

    public List<String> getExercises() {
        return mAdapter.getSelectedExercises();
    }

    public void edit(boolean editMode) {
        mEditMode = editMode;
        mExerciseRequest.setEnabled(mEditMode);
        mExerciseGrid.setEnabled(mEditMode);
        mAdapter.setEnabled(mEditMode);
    }

    private void initWithExercises() {
        if (!isDetached() && getActivity() != null && mExercises != null) {
            if (mAdapter == null) {
                mService.getExercises().enqueue(this);
            } else {
                mAdapter.setSelectedExercises(new ArrayList<>(mExercises));
            }
        }
    }

    public interface OnExercisesChangedListener {
        void onExercisesChanged(List<String> exercises);
    }
}