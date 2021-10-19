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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAllocatedTaskFragment extends Fragment {
    private static final String TAG = "EditAllocTaskFrag";
    private String setDt;
    private OpenTask openTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_allocated_task, container, false);

        openTask = (OpenTask) getArguments().getSerializable(OpenTaskHolder.EDIT_OPEN_TASK);

        EditText task = root.findViewById(R.id.task_name);
        task.setText(openTask.getTask());
        EditText desc = root.findViewById(R.id.task_desc);
        desc.setText(openTask.getDescription());

        Spinner repeat = root.findViewById(R.id.repeat_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.repeat_task_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeat.setAdapter(adapter);
        String rep = openTask.getRepeat();
        if (rep == null) rep = getResources().getString(R.string.blank_spinner);
        repeat.setSelection(adapter.getPosition(rep));
        /*repeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        Spinner alloc = root.findViewById(R.id.allocTo_spinner);
        ArrayAdapter<String> allocAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, MainActivity.spinnerEmployeeList);
        allocAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alloc.setAdapter(allocAdapter);
        String al = openTask.getAlloc();
        if (al == null) al = getResources().getString(R.string.blank_spinner);
        Log.d(TAG, "onCreateView: " + openTask.getAlloc());
        int pos = allocAdapter.getPosition(al);
        if (pos < 0) pos = 0;
        alloc.setSelection(pos);

        TextView date = root.findViewById(R.id.target_date);
        setDt = openTask.getTarget_end();//subject to change
        if (setDt == null) setDt = getResources().getString(R.string.blank_spinner);
        date.setText(setDt);
        date.setOnClickListener(v -> {
            DatePickerFragment dpf = DatePickerFragment.newInstance(setDt);//format and date to be changed
            FragmentManager fm = getParentFragmentManager();
            fm.setFragmentResultListener(DatePickerFragment.RESULT_DATE, dpf, (requestKey, result) -> {
                if (requestKey.equals(DatePickerFragment.RESULT_DATE)) {
                    setDt = result.getString(DatePickerFragment.ARG_DATE);
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
        String st = openTask.getStatus();
        if (st == null) st = getResources().getString(R.string.blank_spinner);
        status.setSelection(adapter.getPosition(st));

        Button submit = root.findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            int id = openTask.getId();

            NavController navController = Navigation.findNavController(root);

            NewTask newTask = new NewTask(task.getText().toString(), desc.getText().toString(), repeat.getSelectedItem().toString()
                    , date.getText().toString(), MainActivity.employeeIdList.get(alloc.getSelectedItemPosition() - 1), status.getSelectedItem().toString());

            LoginActivity.dataFetcher.updateAllocatedTask(id, newTask).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d(TAG, "onUpdateAllocTResponse: " + response.code());
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
                    Log.e(TAG, "onUpdateAllocTFailure: ", t.getCause());
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