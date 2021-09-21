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
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaluateFragment extends Fragment {
    private static final String TAG = "EvaluateFrag";

    static class Evaluation {
        private final Integer emp_score;
        private final String emp_cmnt;

        public Evaluation(Integer emp_score, String emp_cmnt) {
            this.emp_score = emp_score;
            this.emp_cmnt = emp_cmnt;
        }

        public Integer getEmp_score() {
            return emp_score;
        }

        public String getEmp_cmnt() {
            return emp_cmnt;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_evaluate, container, false);

        MyEvaluationFragment.MyEvaluation myEvaluation = (MyEvaluationFragment.MyEvaluation) getArguments().getSerializable(MyEvaluationFragment.TAG);

        TextView jobReq = root.findViewById(R.id.job_req);
        jobReq.setText(myEvaluation.getJob_req());

        EditText comments = root.findViewById(R.id.comm);
        comments.setText(myEvaluation.getComments());

        Spinner sscore = root.findViewById(R.id.sscore_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.rating_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sscore.setAdapter(adapter);
        Integer ss = myEvaluation.getEmp_score();
        if (ss == null) ss = 0;
        sscore.setSelection(ss);

        Button submit = root.findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            if (sscore.getSelectedItemPosition() < 1) {
                Toast.makeText(getContext(), "Invalid self score", Toast.LENGTH_SHORT).show();
                return;
            }

            Evaluation evaluation = new Evaluation(sscore.getSelectedItemPosition(), comments.getText().toString());
            LoginActivity.dataFetcher.submitMyEvaluation(myEvaluation.getId(), evaluation).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                    Log.d(TAG, "onResponse: " + response.code());
                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Navigation.findNavController(root).navigateUp();
                }

                @Override
                public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                    Log.e(TAG, "onFailure: ", t.getCause());
                    Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(root).navigateUp();
                }
            });
        });

        return root;
    }
}