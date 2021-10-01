package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenTaskHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "OpenTaskHolder";
    public static final String EDIT_OPEN_TASK = "edit_task";
    public static final String VIEW_OPEN_TASK = "view_task";
    public static final String COMM_OPEN_TASK = "comm_task";
    private final TextView sr;
    private final TextView task;
    private final TextView alloc;
    private final TextView allocDate;
    private final TextView targetDate;
    private final TextView status;
    private final ImageButton actionEdit;
    private final ImageButton actionComment;
    private final ImageButton actionDelete;
    private OpenTask openTask;

    public OpenTaskHolder(@NonNull @NotNull View itemView, boolean hideDelBtn, View view, OpenTasksAdapter adapter) {
        super(itemView);

        sr = itemView.findViewById(R.id.sr);
        task = itemView.findViewById(R.id.task);
        alloc = itemView.findViewById(R.id.alloc);
        allocDate = itemView.findViewById(R.id.allocD);
        targetDate = itemView.findViewById(R.id.targetD);
        status = itemView.findViewById(R.id.status);
        actionEdit = itemView.findViewById(R.id.edit_task);
        actionComment = itemView.findViewById(R.id.comm_task);
        actionDelete = itemView.findViewById(R.id.del_task);

        NavController navController = Navigation.findNavController(view);

        task.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putSerializable(VIEW_OPEN_TASK, openTask);
            if (hideDelBtn)
                navController.navigate(R.id.action_myTasksFragment_to_taskDetailsFragment, b);
            else
                navController.navigate(R.id.action_tasksAllocatedFragment_to_taskDetailsFragment, b);
        });

        actionEdit.setOnClickListener(v -> {
            if (openTask.getStatus() != null && openTask.getStatus().equals(OpenTask.CLOSED)) {
                Toast.makeText(v.getContext(), "Actions are disabled on closed tasks", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle b = new Bundle();
            b.putSerializable(EDIT_OPEN_TASK, openTask);
            if (!hideDelBtn)
                navController.navigate(R.id.action_tasksAllocatedFragment_to_editTaskFragment, b);
            else
                navController.navigate(R.id.action_myTasksFragment_to_editMyTaskFragment, b);
        });

        actionComment.setOnClickListener(v -> {
            if (openTask.getStatus() != null && openTask.getStatus().equals(OpenTask.CLOSED)) {
                Toast.makeText(v.getContext(), "Actions are disabled on closed tasks", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle b = new Bundle();
            b.putInt(COMM_OPEN_TASK, openTask.getId());

            if (hideDelBtn) {//event happened from my task frag
                navController.navigate(R.id.action_myTasksFragment_to_commentTaskFragment, b);
            } else {
                navController.navigate(R.id.action_tasksAllocatedFragment_to_commentTaskFragment, b);
            }
        });

        if (hideDelBtn) {
            actionDelete.setVisibility(View.INVISIBLE);
            actionDelete.setEnabled(false);
        } else {
            actionDelete.setOnClickListener(v -> {
                if (openTask.getStatus() != null && openTask.getStatus().equals(OpenTask.CLOSED)) {
                    Toast.makeText(v.getContext(), "Actions are disabled on closed tasks", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete task?")
                        .setPositiveButton("Yes", (dialog, which) -> LoginActivity.dataFetcher.deleteTask(openTask.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Log.d(TAG, "onDelResponse: " + response.code());
                                try {
                                    if (!response.isSuccessful()) {
                                        Toast.makeText(MainActivity.ctx.get(), "Problem occurred", Toast.LENGTH_SHORT).show();
                                    } else {
                                        int pos = getAdapterPosition();
                                        adapter.getTasks().remove(pos);
                                        adapter.notifyItemRemoved(pos);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e(TAG, "onFailure: ", t.getCause());
                                try {
                                    Toast.makeText(MainActivity.ctx.get(), "Problem occurred", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }))
                        .setNegativeButton("No", null)
                        .create();

                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(view.getContext().getResources().getColor(R.color.black));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(view.getContext().getResources().getColor(R.color.black));
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
    }
}
