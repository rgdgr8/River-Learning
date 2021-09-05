package com.rgdgr8.riverlearning;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenTaskHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "OpenTaskHolder";
    public static final String EDIT_OPEN_TASK = "open_task";
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
    private OpenTask openTask;

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
            Bundle b = new Bundle();
            b.putSerializable(EDIT_OPEN_TASK, openTask);
            if (!hideDelBtn)
                navController.navigate(R.id.action_tasksAllocatedFragment_to_editTaskFragment, b);
            else
                navController.navigate(R.id.action_myTasksFragment_to_editMyTaskFragment, b);
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
            actionDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginActivity.dataFetcher.deleteTask(openTask.getId()).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d(TAG, "onDelResponse: " + response.code());
                                        if (!response.isSuccessful()) {
                                            Toast.makeText(v.getContext(), "Problem occurred", Toast.LENGTH_SHORT).show();
                                        }


                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e(TAG, "onFailure: ", t.getCause());
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }
    }

    public void bind(OpenTask tsk) {
        openTask = tsk;
        sr.setText((getAdapterPosition() + 1) + "");
        task.setText(tsk.getTask());
        alloc.setText(tsk.getAlloc());
        allocDate.setText(tsk.getAllocation_date());
        targetDate.setText(tsk.getTarget_end());
        status.setText(tsk.getStatus());

        if (tsk.getStatus() != null && tsk.getStatus().equals(OpenTask.CLOSED)) {
            actionEdit.setEnabled(false);
            actionComment.setEnabled(false);
            actionDelete.setEnabled(false);
        }
    }
}
