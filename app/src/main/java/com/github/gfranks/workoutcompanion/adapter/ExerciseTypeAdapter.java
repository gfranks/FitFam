package com.github.gfranks.workoutcompanion.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.fragment.ExerciseTypeFragment;

import java.util.List;

public class ExerciseTypeAdapter extends RecyclerView.Adapter<ExerciseTypeAdapter.CheckBoxViewHolder> {

    private List<String> mExercises;
    private List<String> mSelectedExercises;
    private boolean mEnabled;
    private ExerciseTypeFragment.OnExercisesChangedListener mOnExercisesChangedListener;

    public ExerciseTypeAdapter(List<String> exercises, List<String> selectedExercises, boolean enabled, ExerciseTypeFragment.OnExercisesChangedListener onExercisesChangedListener) {
        mExercises = exercises;
        mSelectedExercises = selectedExercises;
        mEnabled = enabled;
        mOnExercisesChangedListener = onExercisesChangedListener;
    }

    public ExerciseTypeAdapter(List<String> exercises, List<String> selectedExercises, boolean enabled) {
        this(exercises, selectedExercises, enabled, null);
    }

    public void setOnExercisesChangedListener(ExerciseTypeFragment.OnExercisesChangedListener onExercisesChangedListener) {
        mOnExercisesChangedListener = onExercisesChangedListener;
    }

    public List<String> getExercises() {
        return mExercises;
    }

    public void setExercises(List<String> exercises, List<String> selectedExercises) {
        mExercises = exercises;
        mSelectedExercises = selectedExercises;
        notifyDataSetChanged();
    }

    public void setSelectedExercises(List<String> selectedExercises) {
        mSelectedExercises = selectedExercises;
        notifyDataSetChanged();
    }

    public List<String> getSelectedExercises() {
        return mSelectedExercises;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
        notifyDataSetChanged();
    }

    public String getItem(int position) {
        return mExercises.get(position);
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    @Override
    public CheckBoxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CheckBoxViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_exercise_type_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CheckBoxViewHolder holder, int position) {
        holder.itemView.setEnabled(mEnabled);
        holder.populate(getItem(position));
    }

    class CheckBoxViewHolder extends RecyclerView.ViewHolder {

        CheckBoxViewHolder(View view) {
            super(view);
        }

        void populate(String exercise) {
            ((CheckBox) itemView).setOnCheckedChangeListener(null); // clear listener if any exists
            ((CheckBox) itemView).setChecked(mSelectedExercises.contains(exercise));
            ((CheckBox) itemView).setText(getItem(getAdapterPosition()));
            ((CheckBox) itemView).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        mSelectedExercises.add(getItem(getAdapterPosition()));
                    } else {
                        mSelectedExercises.remove(getItem(getAdapterPosition()));
                    }

                    if (mOnExercisesChangedListener != null) {
                        mOnExercisesChangedListener.onExercisesChanged(getSelectedExercises());
                    }
                }
            });
        }
    }
}