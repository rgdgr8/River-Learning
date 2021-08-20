package com.rgdgr8.riverlearning;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

    public OpenTaskHolder(@NonNull @NotNull View itemView, boolean hideDelBtn) {
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

        task.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "Task Details", Toast.LENGTH_SHORT).show());

        actionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(itemView.getContext(), "Update Task", Toast.LENGTH_SHORT).show();
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

    public void bind(OpenTask tsk) {
        sr.setText(String.valueOf(tsk.getId()));
        task.setText(tsk.getTask());
        alloc.setText(tsk.getAllocated_to());
        allocDate.setText(tsk.getAllocation_date());
        targetDate.setText(tsk.getTarget_end());
        status.setText(tsk.getStatus());
    }
}
