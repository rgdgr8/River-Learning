package com.rgdgr8.riverlearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

public class EditAllocatedTaskFragment extends Fragment {
    public static final String RESULT_DATE = "result_date";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_allocated_task, container, false);

        Spinner repeat = root.findViewById(R.id.repeat_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.repeat_task_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeat.setAdapter(adapter);
        repeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner alloc = root.findViewById(R.id.allocTo_spinner);
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.allocatedto_task_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alloc.setAdapter(adapter);

        TextView date = root.findViewById(R.id.target_date);
        date.setOnClickListener(v -> {
            DatePickerFragment dpf = DatePickerFragment.newInstance("01/01/2001");
            //dpf.setTargetFragment();
            FragmentManager fm = getParentFragmentManager();
            fm.setFragmentResultListener(RESULT_DATE, dpf, (requestKey, result) -> {
                if (requestKey.equals(RESULT_DATE)) {
                    String dt = result.getString(DatePickerFragment.ARG_DATE);
                    date.setText(dt);
                }
            });
            dpf.show(fm, "DateDialog");//show dialog
        });

        Spinner status = root.findViewById(R.id.status_spinner);
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.task_status_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);

        Button submit = root.findViewById(R.id.submit);
        submit.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_editAllocatedTaskFragment_to_tasksAllocatedFragment));

        return root;
    }
}