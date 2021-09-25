package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailsFragment extends Fragment {

    private static final String TAG = "TaskDeetsFrag";

    static class TaskDetails {
        private Integer work_quality;
        private String comment;
        private String end_date;

        public Integer getWork_quality() {
            return work_quality;
        }

        public String getComment() {
            return comment;
        }

        public String getEnd_date() {
            return end_date;
        }

        public TaskDetails(Integer work_quality, String comment, String end_date) {
            this.work_quality = work_quality;
            this.comment = comment;
            this.end_date = end_date;
        }
    }

    private OpenTask openTask;
    private TaskDetails taskDetails;
    private View root;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        openTask = (OpenTask) getArguments().getSerializable(OpenTaskHolder.VIEW_OPEN_TASK);
        LoginActivity.dataFetcher.getTaskDetails(openTask.getId()).enqueue(new Callback<TaskDetails>() {
            @Override
            public void onResponse(Call<TaskDetails> call, Response<TaskDetails> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if (response.isSuccessful()) {
                    taskDetails = response.body();
                    if (taskDetails != null) {
                        CardView taskAssess = root.findViewById(R.id.task_ass);
                        if (taskDetails.getComment() == null && taskDetails.getWork_quality() == null) {
                            taskAssess.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
                        } else {
                            TextView score = root.findViewById(R.id.score);
                            score.setText(String.valueOf(taskDetails.getWork_quality()));
                            TextView comment = root.findViewById(R.id.comment);
                            comment.setText(taskDetails.getComment());
                        }

                        CardView closingDate = root.findViewById(R.id.closing_date);
                        if (taskDetails.getEnd_date() == null || taskDetails.getEnd_date().equals("")) {
                            closingDate.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
                        } else {
                            TextView date = root.findViewById(R.id.date);
                            date.setText(taskDetails.getEnd_date());
                        }
                    } else {
                        Toast.makeText(getContext(), "Empty Body", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskDetails> call, Throwable t) {
                Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: ", t.getCause());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_task_details, container, false);

        TextView desc = root.findViewById(R.id.desc);
        desc.setText(openTask.getDescription());
        TextView allocBy = root.findViewById(R.id.alloc_by);
        allocBy.setText(openTask.getAlloc());
        TextView allocDate = root.findViewById(R.id.alloc_date);
        allocDate.setText(openTask.getAllocation_date());
        TextView status = root.findViewById(R.id.status);
        status.setText(openTask.getStatus());

        return root;
    }
}