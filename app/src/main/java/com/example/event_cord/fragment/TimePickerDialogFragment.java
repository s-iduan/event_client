package com.example.event_cord.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerDialogFragment extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;

    public TimePickerDialogFragment( TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        mOnTimeSetListener = onTimeSetListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle bunder) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), mOnTimeSetListener, hour, minutes, false);
    }
}
