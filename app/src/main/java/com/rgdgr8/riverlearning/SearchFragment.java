package com.rgdgr8.riverlearning;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

public class SearchFragment<T> extends DialogFragment {
    public static final String TAG = "SearchFrag";
    private final int layoutId;
    public static final String FILTER_RESULT = "filter_result";
    public static final String STATUS = "stat";
    public static final String DATE = "date";
    private final String key;
    private String date;

    public SearchFragment(int contentLayoutId, String key) {
        super(contentLayoutId);
        layoutId = contentLayoutId;
        this.key = key;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(layoutId, null);

        String statusKey = key + STATUS;
        String dateKey = key + DATE;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(TAG+key, Context.MODE_PRIVATE);
        int statusInd = sharedPreferences.getInt(statusKey, 0);
        date = sharedPreferences.getString(dateKey, getActivity().getResources().getString(R.string.blank_spinner));

        TextView targetEndFilter = root.findViewById(R.id.target_end_filter);
        targetEndFilter.setText(date);
        targetEndFilter.setOnClickListener(v -> {
            DatePickerFragment dpf = DatePickerFragment.newInstance(date);
            FragmentManager fm = getParentFragmentManager();
            fm.setFragmentResultListener(DatePickerFragment.RESULT_DATE, dpf, (requestKey, result) -> {
                if (requestKey.equals(DatePickerFragment.RESULT_DATE)) {
                    date = result.getString(DatePickerFragment.ARG_DATE);
                    targetEndFilter.setText(date);
                }
            });
            dpf.show(fm, "DateDialog");//show dialog
        });

        Spinner statusFilter = root.findViewById(R.id.status_filter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.task_status_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusFilter.setAdapter(adapter);
        statusFilter.setSelection(statusInd);

        return new AlertDialog.Builder(requireActivity())
                .setTitle("Filter")
                .setView(root)
                .setPositiveButton("Search", (dialog, which) -> {
                    sharedPreferences.edit().putInt(statusKey, statusFilter.getSelectedItemPosition()).apply();
                    sharedPreferences.edit().putString(dateKey, targetEndFilter.getText().toString()).apply();
                    sendResult((String) statusFilter.getSelectedItem(), targetEndFilter.getText().toString());
                })
                .setNeutralButton("Reset", (dialog, which) -> {
                    sharedPreferences.edit().remove(statusKey).apply();
                    sharedPreferences.edit().putString(dateKey, null).apply();

                    statusFilter.setSelection(0);
                    targetEndFilter.setText(getActivity().getResources().getString(R.string.blank_spinner));
                    sendResult((String) statusFilter.getSelectedItem(), targetEndFilter.getText().toString());
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onStop() {
        super.onStop();

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    private void sendResult(String status, String date) {
        FragmentManager fm = getParentFragmentManager();
        Bundle b = new Bundle();
        b.putString(key + STATUS, status.toLowerCase());
        b.putString(key + DATE, date);
        fm.setFragmentResult(FILTER_RESULT, b);
    }
}
