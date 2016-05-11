package com.spirit.movies.model.firebase;

import com.firebase.client.Firebase;

/**
 * Created by spirit on 12/10/2015.
 */
public abstract class SyncAction
{
    public abstract boolean sync(Firebase firebase);
}
