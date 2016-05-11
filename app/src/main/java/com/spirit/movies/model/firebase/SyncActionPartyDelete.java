package com.spirit.movies.model.firebase;

import com.firebase.client.Firebase;
import com.spirit.movies.model.Party;

/**
 * Created by spirit on 12/10/2015.
 */
public class SyncActionPartyDelete extends SyncAction
{
    private String _id;

    /**
     *
     * @param id
     */
    public SyncActionPartyDelete(String id)
    {
        _id = id;
    }

    /**
     *
     * @param firebase
     * @return
     */
    public boolean sync(Firebase firebase)
    {
        firebase.child("parties").child(_id).removeValue();

        return true;
    }
}
