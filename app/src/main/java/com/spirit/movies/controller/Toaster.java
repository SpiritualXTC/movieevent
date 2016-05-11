package com.spirit.movies.controller;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by spirit on 11/10/2015.
 */
public class Toaster
{
    /**
     *
     * @param view
     * @param resourceId
     * @param duration
     * @return
     */
    public static boolean displayToast(View view, int resourceId, final int duration)
    {
        final String s = view.getContext().getString(resourceId);
        final Context c = view.getContext();

        view.post(new Runnable()
        {
            @Override
            public void run()
            {
                // Display a Toast for feedback to the use
                Toast toast = Toast.makeText(c, s, duration);
                toast.show();
            }
        });


        return true;
    }


    /**
     * @param activity
     * @param resourceId
     * @param duration
     * @return
     */
    public static boolean displayToast(final Activity activity, int resourceId, final int duration)
    {
        final String s = activity.getApplicationContext().getString(resourceId);

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Display a Toast for feedback to the use
                Toast toast = Toast.makeText(activity, s, duration);
                toast.show();
            }
        });


        return true;
    }
}
