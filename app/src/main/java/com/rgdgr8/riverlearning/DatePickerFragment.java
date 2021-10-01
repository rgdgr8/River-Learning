package com.rgdgr8.riverlearning;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

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
    public static final String ARG_DATE = "date";
    public static final String RESULT_DATE = "result_date";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String dt = getArguments().getString(ARG_DATE);
        Date date = null;//might change
        try {
            //date = new SimpleDateFormat("MM dd, yyyy").parse(dt);
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dt);
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

        return new AlertDialog.Builder(requireActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = (mDatePicker.getMonth() + 1);
                        int day = mDatePicker.getDayOfMonth();
                        //sendResult(day,month,year);
                        sendResult(getFormattedDate(day, month, year));
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.theme_blue));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.theme_blue));
    }

    @Override
    public void onStop() {
        super.onStop();

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public static String getFormattedDate(int day, int month, int year) {
        String m = month + "";
        if (month < 10)
            m = "0" + m;
        String d = day + "";
        if (day < 10)
            d = "0" + d;

        return year + "-" + m + "-" + d;
    }

    public static String getMonth(int month) {
        while (true)
            switch (month) {
                case 1:
                    return "Jan";
                case 2:
                    return "Feb";
                case 3:
                    return "Mar";
                case 4:
                    return "April";
                case 5:
                    return "May";
                case 6:
                    return "June";
                case 7:
                    return "July";
                case 8:
                    return "Aug";
                case 9:
                    return "Sept";
                case 10:
                    return "Oct";
                case 11:
                    return "Nov";
                case 12:
                    return "Dec";
                default:
                    month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            }
    }

    private void sendResult(String date) {
        FragmentManager fm = getParentFragmentManager();
        Bundle b = new Bundle();
        b.putString(ARG_DATE, date);
        fm.setFragmentResult(RESULT_DATE, b);
    }

    public static DatePickerFragment newInstance(String date) {
        Bundle b = new Bundle();
        b.putString(ARG_DATE, date);
        DatePickerFragment dpf = new DatePickerFragment();
        dpf.setArguments(b);
        return dpf;
    }
}
