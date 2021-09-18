package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuickFeedBackActivity extends AppCompatActivity {
    public static final String TAG = "FeedbackActivity";

    static class Feedback {
        private Integer feedback_to;
        private Integer rating;
        private String details;

        public Feedback(Integer feedback_to, Integer rating, String details) {
            this.feedback_to = feedback_to;
            this.rating = rating;
            this.details = details;
        }

        public Integer getFeedback_to() {
            return feedback_to;
        }

        public Integer getRating() {
            return rating;
        }

        public String getDetails() {
            return details;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_feed_back);

        getSupportActionBar().setTitle("Quick Feedback");

        Spinner feedbackFor = findViewById(R.id.feedback_for);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MainActivity.spinnerEmployeeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedbackFor.setAdapter(adapter);

        Spinner rating = findViewById(R.id.rating);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.rating_spinner, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rating.setAdapter(adapter2);

        EditText ifeel = findViewById(R.id.ifeel);

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            if (rating.getSelectedItemPosition() < 1 || feedbackFor.getSelectedItemPosition() < 1) {
                Toast.makeText(this, "Invalid Selection(s)", Toast.LENGTH_SHORT).show();
                return;
            }

            Feedback feedback = new Feedback(MainActivity.employeeIdList.get(feedbackFor.getSelectedItemPosition() - 1)
                    , rating.getSelectedItemPosition(), ifeel.getText().toString());

            LoginActivity.dataFetcher.iFeel(feedback).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d(TAG, "onResponse: " + response.code());
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    finish();
                    Log.e(TAG, "onFailure: ", t.getCause());
                }
            });
        });
    }
}