package com.rgdgr8.riverlearning;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OpenTasksAdapter extends RecyclerView.Adapter<OpenTaskHolder> {
    private static final String TAG = "OpenTaskAdapter";
    private final List<OpenTask> tasks;
    private final Fragment fragment;
    private final int layoutId;
    private final boolean hideDelBtn;

    public List<OpenTask> getTasks() {
        return tasks;
    }

    public OpenTasksAdapter(Fragment fragment, List<OpenTask> tasks, int layoutId, boolean hideDelBtn) {
        this.tasks = tasks;
        this.fragment = fragment;
        this.layoutId = layoutId;
        this.hideDelBtn = hideDelBtn;
    }

    @NonNull
    @NotNull
    @Override
    public OpenTaskHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = fragment.requireActivity().getLayoutInflater().inflate(layoutId, parent, false);
        return new OpenTaskHolder(v, hideDelBtn, fragment.getView(), this);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OpenTaskHolder holder, int position) {
        //Log.d(TAG, "onBindViewHolder: "+tasks.get(position));
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
