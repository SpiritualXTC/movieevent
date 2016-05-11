package com.spirit.movies.model;

/**
 * Created by unfor on 17/10/2015.
 */
public interface IPartyListener
{
    void onPartyInvite(String id);
    void onPartyUpdate(Party party);
}
