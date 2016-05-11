package com.spirit.movies.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by s3435406 on 25/08/2015.
 */
public class DateDialog extends DialogFragment
{
    private static int USE_CURRENT_TIME = -1;

    private DatePickerDialog.OnDateSetListener _listener = null;

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

        int year = arguments.getInt("year", USE_CURRENT_TIME);
        int month = arguments.getInt("month", USE_CURRENT_TIME);
        int day = arguments.getInt("day", USE_CURRENT_TIME);

        if (year == USE_CURRENT_TIME)
            year = c.get(Calendar.YEAR);

        if (month == USE_CURRENT_TIME)
            month = c.get(Calendar.MONTH);

        if (day == USE_CURRENT_TIME)
            day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), _listener, year, month, day);
    }

    /**
     * Sets the Event Listener
     * @param listener
     */
    public void setOnDateListener(DatePickerDialog.OnDateSetListener listener)
    {
        _listener = listener;
    }
}
