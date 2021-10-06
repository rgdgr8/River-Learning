package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssessmentOfTaskFragment extends Fragment {
    private static final String TAG = "AssessmentOfTaskFrag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_assessment_of_task, container, false);

        AssessTasksFragment.AssessTask assessTask = (AssessTasksFragment.AssessTask) getArguments().getSerializable(AssessTasksFragment.TAG);

        TextView task = root.findViewById(R.id.task);
        task.setText(assessTask.getTask());

        Spinner performance = root.findViewById(R.id.performance);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.performance_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        performance.setAdapter(adapter);
        Integer perf = assessTask.getPerformance();
        if (perf == null || perf < 1) perf = AssessTasksFragment.score_scale.length;
        performance.setSelection(AssessTasksFragment.score_scale.length - perf);

        EditText comment = root.findViewById(R.id.comment);
        comment.setText(assessTask.getComments());

        Button submit = root.findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            if (performance.getSelectedItemPosition() < 1) {
                Toast.makeText(getActivity(), "Select a valid performance score", Toast.LENGTH_SHORT).show();
                return;
            }

            NavController navController = Navigation.findNavController(root);
            //AssessedTask assessedTask = new AssessedTask(null, performance.getSelectedItemPosition(), comment.getText().toString(), null);
            AssessedTask assessedTask = new AssessedTask(AssessTasksFragment.score_scale.length - performance.getSelectedItemPosition(), comment.getText().toString());
            LoginActivity.dataFetcher.submitTaskAssessment(assessTask.getId(), assessedTask).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d(TAG, "onResponse: " + response.code());
                    try {
                        if (!response.isSuccessful()) {
                            Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    navController.navigateUp();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "onFailure: ", t.getCause());

                    try {
                        Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                        MainActivity.checkNetworkAndShowDialog(getActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    navController.navigateUp();
                }
            });
        });

        return root;
    }

    static class AssessedTask {
        private final Integer work_quality;
        private final String comment;

        public AssessedTask(Integer work_quality, String comment) {
            this.work_quality = work_quality;
            this.comment = comment;
        }

        public Integer getWork_quality() {
            return work_quality;
        }

        public String getComment() {
            return comment;
        }
    }
}