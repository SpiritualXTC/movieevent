package com.spirit.movies.model.firebase;

import android.content.Context;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.spirit.movies.model.IPartyListener;
import com.spirit.movies.model.MovieFacade;
import com.spirit.movies.model.Party;
import com.spirit.movies.util.ConvertUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by spirit on 12/10/2015.
 */
public class FirebaseHelper
{
    static private FirebaseHelper _instance = null;
    static synchronized public FirebaseHelper getInstance()
    {
        if (_instance == null)
            _instance = new FirebaseHelper();

        return _instance;
    }



    private Context _context = null;
    private Firebase _firebase = null;

    // List of Sync Actions
    private ArrayList<SyncAction> _sync;


    private ArrayList<IPartyListener> _partyListeners;


    /**
     *
     */
    private FirebaseHelper()
    {
        // Create Sync List
        _sync = new ArrayList<>();

        _partyListeners = new ArrayList<>();
    }



    /**
     *
     * @param c
     */
    public void initialise(Context c, String userId)
    {
        Log.d("movies", "FIREBASE: Initialise");

        _context = c;

        // Init Firebase
        Firebase.setAndroidContext(c);
        _firebase = new Firebase("https://movie-spirit.firebaseio.com/");

        if (userId != null)
            setUser(userId);

        // Create Internal Listener
        addPartyListener(new IPartyListener()
        {
            @Override
            public void onPartyInvite(String id)
            {
                listenToParty(id);
            }

            @Override
            public void onPartyUpdate(Party party)
            {

            }
        });
    }


    /**
     * Sets up the Listener
     * @param userId
     */
    public void setUser(String userId)
    {
        if (userId == null)
            throw new IllegalArgumentException();

//        String id = FirebaseHelper.emailToUserId(email);

        Log.d("movies", "FIREBASE: Listening for changes to user = " + userId);

        // Register for Events
        _firebase.child("invites").child(userId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("movies", "FIREBASE: Data Changed");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError)
            {
                Log.d("movies", "FIREBASE: Data Cancelled");
            }
        });

        _firebase.child("invites").child(userId).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                firePartyInviteEvent(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                Log.d("movies", "FIREBASE: You got uninvited to a party! Haha");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError)
            {

            }
        });
    }



    /**
     *
     */
    public void sync()
    {
        if (_firebase == null)
            throw new IllegalStateException();

        // Currently Connected to the Interwebs
        if (MovieFacade.getInstance().isConnected() == false)
        {
            return;
        }

        // Continue until nothing to sync... or something fails.
        while (! _sync.isEmpty())
        {
            // Get First Sync Action
            SyncAction action = _sync.get(0);

            // Attempt Sync
            if (action.sync(_firebase) == false)
                break;

            // Remove Action
            _sync.remove(0);
        }
    }


    /**
     * Update a party in the firebase
     */
    public void update(Party party)
    {
        // Create Sync Action
        SyncAction action = new SyncActionPartyUpdate(party);

        // Add Action
        addAction(action);

        // Attemp to Sync Online
        sync();
    }


    /**
     * Delete a party from firebase
     * @param partyId
     */
    public void delete(String partyId)
    {
        //Create Sync Action
        SyncAction action = new SyncActionPartyDelete(partyId);

        // Add Action
        addAction(action);

        // Attempt to Sync Online
        sync();
    }


    /**
     * Adds a Sync Action
     * @param action
     */
    protected void addAction(SyncAction action)
    {
        // Adds the Sync Action
        _sync.add(action);
    }


    /**
     * Add Listener
     * @param listener
     */
    public void addPartyListener(IPartyListener listener)
    {
        _partyListeners.add(listener);
    }


    /**
     * Creates a listener to listen to a specific party
     * @param partyId
     */
    private void listenToParty(String partyId)
    {
        Log.d("movies", "FIREBASE: Listening for changes to party = " + partyId);

        _firebase.child("parties").child(partyId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                firePartyUpdateEvent(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError)
            {

            }
        });


    }


    /**
     *
     */
    private void firePartyInviteEvent(DataSnapshot snapshot)
    {
        Log.d("movies", "FIREBASE: You are invited to a party! Yay");
        String partyId = snapshot.getKey();

        for (IPartyListener e : _partyListeners)
            e.onPartyInvite(partyId);
    }


    /**
     *
     */
    private void firePartyUpdateEvent(DataSnapshot snapshot)
    {
        Log.d("movies", "FIREBASE: Party Invitation Updated");


        String id = snapshot.getKey();
        String venue = snapshot.child("venue").getValue(String.class);
        String movie = snapshot.child("movie").getValue(String.class);
        Date datetime = snapshot.child("datetime").getValue(Date.class);
        String location = snapshot.child("location").getValue(String.class);

        Log.d("movies", "Children: " + snapshot.getChildrenCount());

        Log.d("movies", "Party ID = " + id);
        Log.d("movies", "Party Venue = " + venue);
        Log.d("movies", "Party Movie = " + movie);
        if (datetime != null)
        {
            Log.d("movies", "Party Date = " + ConvertUtil.convertDateString(_context, datetime));
            Log.d("movies", "Party Time = " + ConvertUtil.convertTimeString(_context, datetime));
        }
        Log.d("movies", "Party Location = " + location);


        // Create Party
        Party party = new Party(id, movie);
        party.setVenue(venue);
        party.setLocation(location);
        party.setDate(datetime);

        for (IPartyListener listener : _partyListeners)
            listener.onPartyUpdate(party);
    }

}

