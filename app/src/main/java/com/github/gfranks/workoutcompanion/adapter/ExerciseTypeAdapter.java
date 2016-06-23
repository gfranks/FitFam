package com.github.gfranks.workoutcompanion.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.github.gfranks.workoutcompanion.R;

import java.util.List;

public class ExerciseTypeAdapter extends RecyclerView.Adapter<ExerciseTypeAdapter.CheckBoxViewHolder> {

    private List<String> mExercises;
    private List<String> mSelectedExercises;
    private boolean mEnabled;

    public ExerciseTypeAdapter(List<String> exercises, List<String> selectedExercises, boolean enabled) {
        mExercises = exercises;
        mSelectedExercises = selectedExercises;
        mEnabled = enabled;
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
    public void onBindViewHolder(CheckBoxViewHolder holder, final int position) {
        holder.itemView.setEnabled(mEnabled);
        String exercise = getItem(position);
        ((CheckBox) holder.itemView).setOnCheckedChangeListener(null); // clear listener if any exists
        ((CheckBox) holder.itemView).setChecked(mSelectedExercises.contains(exercise));
        ((CheckBox) holder.itemView).setText(getItem(position));
        ((CheckBox) holder.itemView).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSelectedExercises.add(getItem(position));
                } else {
                    mSelectedExercises.remove(getItem(position));
                }
            }
        });
    }

    class CheckBoxViewHolder extends RecyclerView.ViewHolder {

        CheckBoxViewHolder(View view) {
            super(view);
        }
    }
}