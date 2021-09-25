package com.rgdgr8.riverlearning;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateReportKpiFragment extends Fragment {
    private static final String TAG = "UpdateReportFrag";

    static class UpdatedKpi {
        @SerializedName("actual_value")
        private final Float actual;

        public Float getActual() {
            return actual;
        }

        public UpdatedKpi(Float actual) {
            this.actual = actual;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_update_report_kpi, container, false);

        assert getArguments() != null;
        ReportKpiFragment.Kpi kpi = (ReportKpiFragment.Kpi) getArguments().getSerializable(ReportKpiFragment.TAG);

        TextView kpiTv = root.findViewById(R.id.kpi);
        kpiTv.setText(kpi.getKpi_name());
        TextView target = root.findViewById(R.id.target);
        if (kpi.getTarget_value() != null)
            target.setText(kpi.getTarget_value() + "");
        else
            target.setText(requireActivity().getResources().getString(R.string.blank_spinner));

        EditText actual = root.findViewById(R.id.actual);
        if (kpi.getActual_value() != null)
            actual.setText(kpi.getActual_value() + "");

        Button submit = root.findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            if (actual.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Value cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            UpdatedKpi updatedKpi = new UpdatedKpi(Float.parseFloat(actual.getText().toString()));
            LoginActivity.dataFetcher.submitKpiReport(kpi.getId(), updatedKpi).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                    Log.d(TAG, "onResponse: " + response.code());
                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    }
                    Navigation.findNavController(root).navigateUp();
                }

                @Override
                public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                    Toast.makeText(getContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailure: ", t.getCause());
                    Navigation.findNavController(root).navigateUp();
                }
            });
        });

        return root;
    }
}