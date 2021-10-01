package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingEnrollFragment extends Fragment {
    private static final String TAG = "EnrollFrag";

    static class Enrollment {
        private Integer employee;

        public Integer getEmployee() {
            return employee;
        }

        public Enrollment(Integer employee) {
            this.employee = employee;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training_enroll, container, false);

        MyTrainingsFragment.Trainings.Training training = (MyTrainingsFragment.Trainings.Training) getArguments().getSerializable(MyTrainingsFragment.ENROLL);

        TextView topic = root.findViewById(R.id.topic);
        topic.setText(training.getTopic_name());
        TextView trainer = root.findViewById(R.id.trainer);
        trainer.setText(training.getTrainer_name());
        TextView date = root.findViewById(R.id.date);
        date.setText(training.getDate());

        Spinner alloc = root.findViewById(R.id.employee);
        ArrayAdapter<String> allocAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, MainActivity.spinnerEmployeeList);
        allocAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alloc.setAdapter(allocAdapter);

        Button submit = root.findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            if (alloc.getSelectedItemPosition() < 1) {
                Toast.makeText(MainActivity.ctx.get(), "Invalid Employee Selection", Toast.LENGTH_SHORT).show();
                return;
            }

            NavController navController = Navigation.findNavController(root);

            LoginActivity.dataFetcher.enrollInTraining(training.getId(), new Enrollment(MainActivity.employeeIdList.get(alloc.getSelectedItemPosition() - 1))).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d(TAG, "onResponse: " + response.code());
                    try {
                        if (!response.isSuccessful()) {
                            Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.ctx.get(), "Enrollment Successful", Toast.LENGTH_SHORT).show();
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    navController.navigateUp();
                }
            });
        });

        return root;
    }
}