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
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.data.api.WorkoutCompanionService;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.notification.WCInAppMessageManagerConstants;
import com.urbanairship.UAirship;

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
    private ExerciseAdapter mExerciseAdapter;
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
        if (mExerciseAdapter == null || mExerciseGrid.getAdapter() == null) {
            mExerciseAdapter = new ExerciseAdapter(response.body());
            mExerciseGrid.setAdapter(mExerciseAdapter);
        } else {
            mExerciseAdapter.setExercises(response.body());
        }
    }

    @Override
    public void onFailure(Call<List<String>> call, Throwable t) {
        if (isDetached() || getActivity() == null) {
            return;
        }
        UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getWarningBuilder()
                .setAlert(getString(R.string.error_unable_to_load_exercises))
                .create());
        if (mExerciseAdapter == null || mExerciseGrid.getAdapter() == null) {
            mExerciseAdapter = new ExerciseAdapter(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.exercises))));
            mExerciseGrid.setAdapter(mExerciseAdapter);
        } else {
            mExerciseAdapter.setExercises(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.exercises))));
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
                                List<String> exercises = mExerciseAdapter.getExercises();
                                exercises.add(response.body());
                                mExerciseAdapter.setExercises(exercises);
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                if (isDetached() || getActivity() == null) {
                                    return;
                                }
                                UAirship.shared().getInAppMessageManager().setPendingMessage(WCInAppMessageManagerConstants.getWarningBuilder()
                                        .setAlert(t.getMessage())
                                        .create());
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
        mExerciseAdapter.notifyDataSetChanged();
    }

    private class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.CheckBoxViewHolder> {

        private List<String> mExercises;

        ExerciseAdapter(List<String> exercises) {
            mExercises = exercises;
        }

        void setExercises(List<String> exercises) {
            mExercises = exercises;
            notifyDataSetChanged();
        }

        List<String> getExercises() {
            return mExercises;
        }

        public String getItem(int position) {
            return mExercises.get(position);
        }

        @Override
        public int getItemCount() {
            if (mUser == null) {
                return 0;
            }
            return mExercises.size();
        }

        @Override
        public CheckBoxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CheckBoxViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_exercise_type_item, parent, false));
        }

        @Override
        public void onBindViewHolder(CheckBoxViewHolder holder, final int position) {
            holder.itemView.setEnabled(mEditMode);
            String exercise = getItem(position);
            ((CheckBox) holder.itemView).setOnCheckedChangeListener(null); // clear listener if any exists
            ((CheckBox) holder.itemView).setChecked(mUser.getExercises().contains(exercise));
            ((CheckBox) holder.itemView).setText(getItem(position));
            ((CheckBox) holder.itemView).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mUser.getExercises().add(getItem(position));
                    } else {
                        mUser.getExercises().remove(getItem(position));
                    }
                }
            });
        }

        public class CheckBoxViewHolder extends RecyclerView.ViewHolder {

            public CheckBoxViewHolder(View view) {
                super(view);
            }
        }
    }
}