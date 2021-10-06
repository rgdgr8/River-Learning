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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateReportKpiFragment extends Fragment {
    private static final String TAG = "UpdateReportFrag";

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

            try {
                Double.parseDouble(actual.getText().toString());
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Invalid value", Toast.LENGTH_SHORT).show();
                return;
            }

            NavController navController = Navigation.findNavController(root);

            UpdatedKpi updatedKpi = new UpdatedKpi(Float.parseFloat(actual.getText().toString()));
            LoginActivity.dataFetcher.submitKpiReport(kpi.getId(), updatedKpi).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
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
                public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
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

    static class UpdatedKpi {
        @SerializedName("actual_value")
        private final Float actual;

        public UpdatedKpi(Float actual) {
            this.actual = actual;
        }

        public Float getActual() {
            return actual;
        }
    }
}