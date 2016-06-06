package com.github.gfranks.workoutcompanion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.fragment.base.BaseFragment;

import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;

public class ExerciseTypeFragment extends BaseFragment {

    public static final String TAG = "exercise_type_fragment";

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
        mExerciseAdapter = new ExerciseAdapter(Arrays.asList(getContext().getResources().getStringArray(R.array.exercises)));
        mExerciseGrid.setAdapter(mExerciseAdapter);
        mExerciseGrid.setEnabled(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(WCUser.EXTRA, mUser);
    }

    public void setUser(WCUser user) {
        mUser = user;
        if (!isDetached() && getActivity() != null) {
            mExerciseAdapter.notifyDataSetChanged();
        }
    }

    public List<String> getExercises() {
        return mUser.getExercises();
    }

    public void edit(boolean editMode) {
        mEditMode = editMode;
        mExerciseGrid.setEnabled(mEditMode);
        mExerciseAdapter.notifyDataSetChanged();
    }

    private class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.CheckBoxViewHolder> {

        private List<String> mExercises;

        public ExerciseAdapter(List<String> exercises) {
            mExercises = exercises;
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