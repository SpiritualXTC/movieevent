package com.spirit.movies.util;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by spirit on 5/10/2015.
 */
public class ConvertUtil
{
    /**
     * Convert the Date to a Date Format String based on user's pref's
     * @param dateTime
     * @return
     */
    static public String convertDateString(Context c, Date dateTime)
    {
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(c);

        return dateFormat.format(dateTime);
    }


    /**
     * Convert the Date to a Time Format String based on user's pref's
     * @param dateTime
     * @return
     */
    static public String convertTimeString(Context c, Date dateTime)
    {
        java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(c);

        return timeFormat.format(dateTime);
    }

    /**
     *
     * @param dateTime
     * @return
     */
    static public String convertDataTimeToDatebase(Date dateTime)
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateTime);
    }


    /**
     *
     * @param datetime
     * @param format
     * @return
     */
    static public Date convertStringToDateTime(String datetime, String format)
    {
        Date date = null;

        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(datetime);
        }
        catch (Exception e)
        {
            Log.e("movies", e.getMessage());
            e.printStackTrace();
        }

        return date;
    }
}
