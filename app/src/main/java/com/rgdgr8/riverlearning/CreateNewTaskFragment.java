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
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewTaskFragment extends Fragment {
    private static final String TAG = "CreateNewTaskFrag";
    private String setDt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_create_new_task, container, false);

        Spinner repeat = root.findViewById(R.id.repeat_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.repeat_task_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeat.setAdapter(adapter);

        Spinner alloc = root.findViewById(R.id.allocTo_spinner);
        ArrayAdapter<String> allocAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, MainActivity.spinnerEmployeeList);
        allocAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alloc.setAdapter(allocAdapter);

        TextView date = root.findViewById(R.id.target_date);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        setDt = DatePickerFragment.getFormattedDate(day, month, year);
        date.setText(setDt);
        date.setOnClickListener(v -> {
            //DatePickerFragment dpf = DatePickerFragment.newInstance(month + " " + day + ", " + year);
            DatePickerFragment dpf = DatePickerFragment.newInstance(setDt);
            FragmentManager fm = getParentFragmentManager();
            fm.setFragmentResultListener(DatePickerFragment.RESULT_DATE, dpf, (requestKey, result) -> {
                if (requestKey.equals(DatePickerFragment.RESULT_DATE)) {
                    setDt = result.getString(DatePickerFragment.ARG_DATE);
                    //date.setText(DatePickerFragment.getMonth(dt[1]) + " " + dt[0] + ", " + dt[2]);
                    date.setText(setDt);
                }
            });
            dpf.show(fm, "DateDialog");//show dialog
        });

        Spinner status = root.findViewById(R.id.status_spinner);
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.task_status_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);

        EditText task = root.findViewById(R.id.task_name);
        EditText desc = root.findViewById(R.id.task_desc);

        Button submit = root.findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            if (task.getText().toString().equals("") || desc.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Task and/or Description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            submit.setEnabled(false);

            NavController navController = Navigation.findNavController(root);

            NewTask newTask = new NewTask(task.getText().toString(), desc.getText().toString(), repeat.getSelectedItem().toString(),
                    date.getText().toString(), alloc.getSelectedItemPosition(), status.getSelectedItem().toString());
            LoginActivity.dataFetcher.createNewTask(newTask).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d(TAG, "onResponse: " + response.code());
                    try {
                        if (!response.isSuccessful()) {
                            Toast.makeText(MainActivity.ctx.get(), "Problem occurred", Toast.LENGTH_SHORT).show();
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
}