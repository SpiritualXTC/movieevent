package com.spirit.movies.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.spirit.movies.model.MovieFacade;
import com.spirit.movies.model.NetworkConnectionManager;

/**
 * Created by spirit on 21/09/2015.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver
{


    /**
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("movies", "RECV: Connection Changed");

        boolean connAvail = false;

        // Get Connection Manager
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check Mobile Connectivity State
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile != null && mobile.isAvailable())
        {
            Log.d("movies", "RECV: Mobile Connection Available");
            connAvail = true;
        }

        // Check Wifi Connectivity State
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.isAvailable())
        {
            Log.d("movies", "RECV: WiFi Connection Available");
            connAvail = true;
        }

        // Connection Available
        if (! connAvail)
        {
            Log.d("movies", "RECV: No Connection is Available");
        }

//        MovieFacade.getInstance().onConnectionChange(connAvail);

        NetworkConnectionManager.getInstance().onConnectionChange(connAvail);
    }
}
