package com.rgdgr8.riverlearning;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class OpenTaskHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "OpenTaskHolder";
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

        actionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(OpenTaskHolder.this.view).navigate(R.id.action_tasksAllocatedFragment_to_editTaskFragment);
            }
        });

        actionComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(itemView.getContext(), "Comment on Task", Toast.LENGTH_SHORT).show();
            }
        });

        if (hideDelBtn) {
            actionDelete.setVisibility(View.INVISIBLE);
            actionDelete.setEnabled(false);
        } else {
            actionDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Delete Task", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void bind(OpenTask tsk,int pos) {
        sr.setText(pos+"");//id is getting artificially filled, might need to change this
        task.setText(tsk.getTask());
        alloc.setText(tsk.getAlloc());
        allocDate.setText(tsk.getAllocation_date());
        targetDate.setText(tsk.getTarget_end());
        status.setText(tsk.getStatus());

        if(tsk.getStatus().equals(OpenTask.CLOSED)){
            actionEdit.setEnabled(false);
            actionComment.setEnabled(false);
            actionDelete.setEnabled(false);
        }
    }
}
