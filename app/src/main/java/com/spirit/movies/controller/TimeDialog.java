package com.spirit.movies.controller;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import java.util.Calendar;

/**
 * Created by s3435406 on 25/08/2015.
 */
public class TimeDialog extends DialogFragment
{
    private static int USE_CURRENT_TIME = -1;

    private TimePickerDialog.OnTimeSetListener _listener = null;


    /**
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();

        Bundle arguments = getArguments();

        int hour = arguments.getInt("hour", USE_CURRENT_TIME);
        int minute = arguments.getInt("minute", USE_CURRENT_TIME);

        if (hour == USE_CURRENT_TIME)
            hour = c.get(Calendar.HOUR_OF_DAY);

        if (minute == USE_CURRENT_TIME)
            minute = c.get(Calendar.MINUTE);

        // Create the TimePicker dialog
        return new TimePickerDialog(getActivity(), _listener, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }


    /**
     * Sets the Event Listener
     * @param listener
     */
    public void setOnTimeListener(TimePickerDialog.OnTimeSetListener listener)
    {
        _listener = listener;
    }
}
