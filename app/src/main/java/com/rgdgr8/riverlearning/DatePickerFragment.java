package com.rgdgr8.riverlearning;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {
    public static String ARG_DATE = "date";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String dt = getArguments().getString(ARG_DATE);
        Date date = null;//might change
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(dt);
            //Toast.makeText(getActivity(), date.toString(), Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        if (date != null)
            calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);
        DatePicker mDatePicker = v.findViewById(R.id.date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth()+1;
                        int day = mDatePicker.getDayOfMonth();
                        String date = day + "/" + month + "/" + year;
                        sendResult(date);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.theme_blue));
        ((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.theme_blue));
    }

    private void sendResult(String date) {
        FragmentManager fm = getParentFragmentManager();
        Bundle b = new Bundle();
        b.putString(ARG_DATE, date);
        fm.setFragmentResult(EditAllocatedTaskFragment.RESULT_DATE, b);
    }

    public static DatePickerFragment newInstance(String date) {
        Bundle b = new Bundle();
        b.putString(ARG_DATE, date);
        DatePickerFragment dpf = new DatePickerFragment();
        dpf.setArguments(b);
        return dpf;
    }
}
