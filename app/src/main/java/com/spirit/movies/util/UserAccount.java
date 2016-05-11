package com.spirit.movies.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.AccountPicker;


/**
 * Created by spirit on 12/10/2015.
 */
public class UserAccount
{
    static private String PREFS_NAME = "movies_pref";
    static private String USER_ID = "user_id";




    /**
     *
     * @param email
     * @return
     */
    static public String emailToUserId(String email)
    {
        int id = email.hashCode();

        return new Integer(id).toString();
    }

    /**
     *
     * @param c
     * @return
     */
    static public String getUserId(Context c)
    {
        // Read Shared Preferneces
        SharedPreferences prefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String id = prefs.getString(USER_ID, null);

        if (id == null || id.equals(""))
            Log.d("movies", "PREF: User Account not selected");
        else
            Log.d("movies", "PREF: User Account = " + id);

        return id;
    }

    /**
     * Sets the User Email Address
     * @param c
     * @param email
     * @return
     */
    static public String setUserEmail(Context c, String email)
    {
        // Convert to User ID
        String userId  = emailToUserId(email);

        // Write Shared Preferneces
        SharedPreferences prefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();

        edit.putString(USER_ID, userId);

        edit.commit();

        // Return User ID
        return userId;
    }
}
