package com.spirit.movies.model.firebase;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.spirit.movies.model.Party;
import com.spirit.movies.util.UserAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by spirit on 12/10/2015.
 */
public class SyncActionPartyUpdate extends SyncAction
{
    private Party _party = null;

    /**
     *
     */
    public SyncActionPartyUpdate(Party p)
    {
        // Make a copy of the party object
        // Copy is made as further changes to the original Party object
        // DO NOT want to be reflected in the sync update.
        // Later changes will
        _party = new Party(p);
    }

    /**
     *
     * @param firebase
     * @return
     */
    public boolean sync(final Firebase firebase)
    {
        /*
        // THIS DOESN"T WORK :(
        // Previous Invites!
        firebase.child("parties").child(_party.getId()).child("invited").addListenerForSingleValueEvent(
                new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // Is the invitee still.... invited?
                        Log.d("movies", "FIREBASE: Children = " + dataSnapshot.getChildrenCount());


                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                        List<String> invite_list= dataSnapshot.getValue(t);

                        if (invite_list == null)
                            return;

                        // Old Invite List
                        for (String invite_old : invite_list)
                        {
                            boolean invited = false;

                            // New Invite List
                            for (String invite_new : _party.getInvited())
                            {
                                // You are still invited. Yay
                                if (invite_old.equals(invite_new))
                                {
                                    invited = true;
                                    break;
                                }
                            }


                            String userId = UserAccount.emailToUserId(invite_old);

                            // Remove Meh from the invite list
                            if (invited == false)
                            {
                                firebase.child("invites").child(userId).child(_party.getId()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError)
                    {

                    }
                }
        );

*/


        // Set Party
        HashMap<String, Object> p = new HashMap<>();

        p.put("datetime", _party.getDateTime());
        p.put("venue", _party.getVenue());
        p.put("location", _party.getLocation());
        p.put("invited", _party.getInvited());
        p.put("movie", _party.getMovieId());

        firebase.child("parties").child(_party.getId()).setValue(p);



        // Add Invites
        for (String email : _party.getInvited())
        {
            String userId = UserAccount.emailToUserId(email);
            firebase.child("invites").child(userId).child(_party.getId()).setValue(false);
        }


        return true;
    }
}
