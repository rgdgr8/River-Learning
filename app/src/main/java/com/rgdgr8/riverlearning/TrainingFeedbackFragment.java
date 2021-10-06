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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingFeedbackFragment extends Fragment {
    private static final String TAG = "TrainingFeedBackFrag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_training_feedback, container, false);

        int trainingId = getArguments().getInt(MyTrainingsFragment.TAG);

        Spinner score = root.findViewById(R.id.score_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.rating_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        score.setAdapter(adapter);

        EditText comment = root.findViewById(R.id.comment);

        Button submit = root.findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            if (score.getSelectedItemPosition() < 1 || comment.getText().toString().equals("")) {
                Toast.makeText(MainActivity.ctx.get(), "Invalid score and/or empty comment", Toast.LENGTH_SHORT).show();
                return;
            }

            NavController navController = Navigation.findNavController(root);

            TrainingFeedback trainingFeedback = new TrainingFeedback(score.getSelectedItemPosition(), comment.getText().toString());
            LoginActivity.dataFetcher.submitTrainingFeedback(trainingId, trainingFeedback).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d(TAG, "onResponse: " + response.code());
                    try {
                        if (!response.isSuccessful()) {
                            Toast.makeText(MainActivity.ctx.get(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.ctx.get(), "Feedback Submitted", Toast.LENGTH_LONG).show();
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

    static class TrainingFeedback {
        private final Integer score;
        private final String comment;

        public TrainingFeedback(Integer score, String comment) {
            this.score = score;
            this.comment = comment;
        }

        public Integer getScore() {
            return score;
        }

        public String getComment() {
            return comment;
        }
    }
}