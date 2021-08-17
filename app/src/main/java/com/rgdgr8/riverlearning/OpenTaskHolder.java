package com.rgdgr8.riverlearning;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class OpenTaskHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "OpenTaskHolder";
    private final TextView sr;
    private final TextView task;
    private final TextView allocTo;
    private final TextView allocDate;
    private final TextView targetDate;
    private final TextView status;

    public OpenTaskHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        sr = itemView.findViewById(R.id.sr);
        task = itemView.findViewById(R.id.task);
        allocTo = itemView.findViewById(R.id.allocTo);
        allocDate = itemView.findViewById(R.id.allocD);
        targetDate = itemView.findViewById(R.id.targetD);
        status = itemView.findViewById(R.id.status);
    }

    public void bind(OpenTask tsk) {
        sr.setText(String.valueOf(tsk.getSr()));
        task.setText(tsk.getTask());
        allocTo.setText(tsk.getAllocTo());
        allocDate.setText(tsk.getAllocDate());
        targetDate.setText(tsk.getTargetDate());
        status.setText((tsk.getStatus() ? "Open" : "Closed"));
    }
}
