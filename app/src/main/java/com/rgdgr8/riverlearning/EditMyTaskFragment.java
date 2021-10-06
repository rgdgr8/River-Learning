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

public class EditMyTaskFragment extends Fragment {
    private static final String TAG = "EditMyTaskFrag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_edit_my_task, container, false);

        OpenTask openTask = (OpenTask) getArguments().getSerializable(OpenTaskHolder.EDIT_OPEN_TASK);

        Spinner status = root.findViewById(R.id.status_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.task_status_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);
        String st = openTask.getStatus();
        if (st == null) st = getResources().getString(R.string.blank_spinner);
        status.setSelection(adapter.getPosition(st));

        EditText name = root.findViewById(R.id.task_name);
        name.setText(openTask.getTask());
        EditText desc = root.findViewById(R.id.task_desc);
        desc.setText(openTask.getDescription());

        Button submit = root.findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(root);

            UpdatedTask updatedTask = new UpdatedTask(name.getText().toString(), desc.getText().toString(), status.getSelectedItem().toString());

            LoginActivity.dataFetcher.updateMyTask(openTask.getId(), updatedTask).enqueue(new Callback<Void>() {
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

    static class UpdatedTask {
        private final String task;
        private final String description;
        private String status;

        public UpdatedTask(String task, String description, String status) {
            this.task = task;
            this.description = description;
            this.status = status;

            if (this.status.charAt(0) == '-')
                this.status = null;
        }

        public String getTask() {
            return task;
        }

        public String getDescription() {
            return description;
        }

        public String getStatus() {
            return status;
        }
    }
}