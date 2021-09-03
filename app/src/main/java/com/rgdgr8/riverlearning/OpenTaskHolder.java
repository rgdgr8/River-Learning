package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class OpenTaskHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "OpenTaskHolder";
    public static final String COMMENT_FROM = "comment_from";
    private final TextView sr;
    private final TextView task;
    private final TextView alloc;
    private final TextView allocDate;
    private final TextView targetDate;
    private final TextView status;
    private final ImageButton actionEdit;
    private final ImageButton actionComment;
    private final ImageButton actionDelete;
    private final View view;

    public OpenTaskHolder(@NonNull @NotNull View itemView, boolean hideDelBtn, View view) {
        super(itemView);

        this.view = view;
        sr = itemView.findViewById(R.id.sr);
        task = itemView.findViewById(R.id.task);
        alloc = itemView.findViewById(R.id.alloc);
        allocDate = itemView.findViewById(R.id.allocD);
        targetDate = itemView.findViewById(R.id.targetD);
        status = itemView.findViewById(R.id.status);
        actionEdit = itemView.findViewById(R.id.edit_task);
        actionComment = itemView.findViewById(R.id.comm_task);
        actionDelete = itemView.findViewById(R.id.del_task);

        task.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "Task Details", Toast.LENGTH_SHORT).show());

        NavController navController = Navigation.findNavController(this.view);

        actionEdit.setOnClickListener(v -> {
            if (!hideDelBtn)
                navController.navigate(R.id.action_tasksAllocatedFragment_to_editTaskFragment);
            else
                navController.navigate(R.id.action_myTasksFragment_to_editMyTaskFragment);
        });

        actionComment.setOnClickListener(v -> {
            /*Bundle b = new Bundle();
            b.putBoolean(COMMENT_FROM, hideDelBtn);*/

            if (hideDelBtn) {//event happened from my task frag
                navController.navigate(R.id.action_myTasksFragment_to_commentTaskFragment);
            } else {
                navController.navigate(R.id.action_tasksAllocatedFragment_to_commentTaskFragment);
            }
        });

        if (hideDelBtn) {
            actionDelete.setVisibility(View.INVISIBLE);
            actionDelete.setEnabled(false);
        } else {
            actionDelete.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "Delete Task", Toast.LENGTH_SHORT).show());
        }
    }

    public void bind(OpenTask tsk, int pos) {
        sr.setText((pos+1) + "");//id is getting artificially filled, might need to change this
        task.setText(tsk.getTask());
        alloc.setText(tsk.getAlloc());
        allocDate.setText(tsk.getAllocation_date());
        targetDate.setText(tsk.getTarget_end());
        status.setText(tsk.getStatus());

        if (tsk.getStatus()!=null && tsk.getStatus().equals(OpenTask.CLOSED)) {
            actionEdit.setEnabled(false);
            actionComment.setEnabled(false);
            actionDelete.setEnabled(false);
        }
    }
}
