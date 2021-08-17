package com.rgdgr8.riverlearning;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OpenTasksAdapter extends RecyclerView.Adapter<OpenTaskHolder> {
    private final List<OpenTask> tasks;
    private final Activity context;
    private final int layoutId;

    public OpenTasksAdapter(Activity context, List<OpenTask> tasks, int layoutId) {
        this.tasks = tasks;
        this.context = context;
        this.layoutId = layoutId;
    }

    @NonNull
    @NotNull
    @Override
    public OpenTaskHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = context.getLayoutInflater().inflate(layoutId,parent,false);
        return new OpenTaskHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OpenTaskHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
