package com.spirit.movies.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

/**
 * Created by spirit on 18/10/2015.
 */
public class NetworkConnectionManager
{
    static private NetworkConnectionManager _instance;

    /**
     *
     * @return
     */
    static synchronized public NetworkConnectionManager getInstance()
    {
        if (_instance == null)
            _instance = new NetworkConnectionManager();

        return _instance;
    }




    private ArrayList<IConnectionChange> _listeners;

    /**
     *
     */
    private NetworkConnectionManager()
    {
        _listeners = new ArrayList<>();

    }

    /**
     * Adds a listener
     * @param listener
     */
    public void addListener(IConnectionChange listener)
    {
        _listeners.add(listener);
    }

    /**
     * Fires Event
     */
    public void onConnectionChange(boolean connected)
    {
        for (IConnectionChange listener : _listeners)
            listener.onConnectionChange(connected);
    }

    /**
     * Check whether the currently connected to the internet
     * @return
     */
    public boolean isConnected(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
