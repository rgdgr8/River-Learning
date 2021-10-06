package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private OpenTask openTask;
    private TaskDetails taskDetails;
    private View root;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openTask = (OpenTask) getArguments().getSerializable(OpenTaskHolder.VIEW_OPEN_TASK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_task_details, container, false);

        TextView desc = root.findViewById(R.id.desc);
        desc.setText(openTask.getDescription());
        TextView allocBy = root.findViewById(R.id.alloc_by);
        allocBy.setText(openTask.getAlloc());
        TextView allocDate = root.findViewById(R.id.alloc_date);
        allocDate.setText(openTask.getAllocation_date());
        TextView status = root.findViewById(R.id.status);
        status.setText(openTask.getStatus());

        LoginActivity.dataFetcher.getTaskDetails(openTask.getId()).enqueue(new Callback<TaskDetails>() {
            @Override
            public void onResponse(Call<TaskDetails> call, Response<TaskDetails> response) {
                Log.d(TAG, "onResponse: " + response.code());
                try {
                    if (response.isSuccessful()) {
                        taskDetails = response.body();
                        if (taskDetails != null) {
                            CardView taskAssess = root.findViewById(R.id.task_ass);
                            if (taskDetails.getComment() == null && taskDetails.getWork_quality() == null) {
                                taskAssess.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                            } else {
                                TextView score = root.findViewById(R.id.score);
                                if (taskDetails.getWork_quality() != null)
                                    score.setText(String.valueOf(taskDetails.getWork_quality()));
                                else
                                    score.setText(MainActivity.ctx.get().getResources().getString(R.string.blank_spinner));

                                TextView comment = root.findViewById(R.id.comment);
                                if (taskDetails.getComment() != null)
                                    comment.setText(taskDetails.getComment());
                                else
                                    comment.setText(MainActivity.ctx.get().getResources().getString(R.string.blank_spinner));
                            }

                            CardView closingDate = root.findViewById(R.id.closing_date);
                            if (taskDetails.getEnd_date() == null || taskDetails.getEnd_date().equals("")) {
                                closingDate.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                            } else {
                                TextView date = root.findViewById(R.id.date);
                                date.setText(taskDetails.getEnd_date());
                            }
                        } else {
                            CardView taskAssess = root.findViewById(R.id.task_ass);
                            taskAssess.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

                            CardView closingDate = root.findViewById(R.id.closing_date);
                            closingDate.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                        }
                    } else {
                        Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TaskDetails> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t.getCause());
                try {
                    Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    MainActivity.checkNetworkAndShowDialog(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    static class TaskDetails {
        private final Integer work_quality;
        private final String comment;
        private final String end_date;

        public TaskDetails(Integer work_quality, String comment, String end_date) {
            this.work_quality = work_quality;
            this.comment = comment;
            this.end_date = end_date;
        }

        public Integer getWork_quality() {
            return work_quality;
        }

        public String getComment() {
            return comment;
        }

        public String getEnd_date() {
            return end_date;
        }
    }
}