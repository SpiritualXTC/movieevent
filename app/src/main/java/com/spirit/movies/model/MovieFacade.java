package com.spirit.movies.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.spirit.movies.model.cache.CacheImageMap;
import com.spirit.movies.model.cache.CacheMovieHelper;
import com.spirit.movies.model.cache.CachePartyHelper;
import com.spirit.movies.model.firebase.FirebaseHelper;
import com.spirit.movies.model.sql.IFeedbackHandler;
import com.spirit.movies.model.sql.SQLHelper;
import com.spirit.movies.util.UserAccount;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by spirit on 2/08/2015.
 */
public class MovieFacade
{
    /**
     * Get Access to the Movie Model
     */
    static private MovieFacade _instance = new MovieFacade();
    static public synchronized MovieFacade getInstance()
    {
        return _instance;
    }



    private Context _context;

    private CachePartyHelper _partyCache;
    private CacheMovieHelper _movieCache = null;
    private CacheImageMap _imageCache = null;


    /**
     * Hidden Constructor
     */
    private MovieFacade()
    {
        // Create the Movie Cache :: Make this so it's not a singleton :)
        _movieCache = CacheMovieHelper.getInstance();

        // Create the Party Cache
        _partyCache = new CachePartyHelper();

        // Create Image Cache
        _imageCache = CacheImageMap.getInstance();

        // Firebase Sync Event
        NetworkConnectionManager.getInstance().addListener(new IConnectionChange()
        {
            @Override
            public void onConnectionChange(boolean connected)
            {
                FirebaseHelper.getInstance().sync();
            }
        });
    }


    /**
     *
     * @param context
     */
    public void initialise(final Context context, final String email)
    {
        // Set Context
        _context = context;

        // Initialise
        new Thread(new Runnable()
        {
            /**
             *
            */
            @Override
            public void run()
            {

                // Initialise Database
                SQLHelper.initialise(context);

                // Load SQL Parties
                ArrayList<Party> parties = SQLHelper.loadParties();

                Log.d("movies", "Loaded " + parties.size() + " parties");

                // Load Parties
                for (Party party : parties)
                    _partyCache.putParty(party);

                // Initialise Firebase
                FirebaseHelper.getInstance().initialise(context, email);
            }
        }).start();
    }


    /**
     * Sets the User ID
     * @param email
     */
    public void setUser(Context c, String email)
    {
        // Sets the User Account
        String userId = UserAccount.setUserEmail(c, email);

        // Sets Firebase to a specific User
        FirebaseHelper.getInstance().setUser(userId);
    }


    /**
     *
     * @return
     */
    public boolean isConnected()
    {
        return NetworkConnectionManager.getInstance().isConnected(_context);
        //    return _network.isConnected();
    }


    /**
     * Cheap hack.... lol
     * @param connected
     * @return
     */
//    public void onConnectionChange(boolean connected)
//    {
//        // Sync Firebase!
////        FirebaseHelper.getInstance().sync();
//
//    }



    /**
     * Update Movie Rating
     * @param movieId
     * @param rating
     */
    public void updateMovieRating(final String movieId, final float rating, final IFeedbackHandler feedback)
    {
        Log.d("movies", "FACADE: Rating Updated.");

        // Get Movie Cache
//        Movie movie = CacheMovieHelper.getInstance().getMovie(movieId);
        Movie movie = _movieCache.getMovie(movieId);


        if (movie != null)
            movie.setRating(rating);

        // Update Movie Rating in SQL Cache
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLHelper.updateMovieRating(movieId, rating);

                if (feedback != null)
                    feedback.onComplete(true);
            }
        }).start();
    }


    /**
     * Get Movie
     * @param id
     * @param detailHandler
     */
    public void getMovie(String id, IMovieDetailHandler detailHandler)
    {
        // Create Async Task
        // Create ASyncTask to pull details view down
        MovieDetailAsyncTask task = new MovieDetailAsyncTask(detailHandler);

        task.execute(id);
    }


    /**
     * Caches the Movie
     * @param movie
     */
    public boolean cacheMovie(Movie movie)
    {
        boolean result = _movieCache.cacheMovie(movie);

        Log.d("movies", "Cache Size = " + _movieCache.Size());

        return result;
    }


    /**
     * Stores the Movie
     * @param movie
     */
    public void storeMovie(final Movie movie)
    {
        // Invalid Movie
        if (movie == null) return;

        // Create Thread
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Send to SQL
                SQLHelper.storeMovie(movie);
            }
        }).start();
    }


    /**
     * Caches the Image
     * @param url
     * @param bitmap
     */
    public void cacheImage(String url, Bitmap bitmap)
    {
        _imageCache.put(url, bitmap);
    }








    /**
     * Create a Party
     * @param movieId
     * @return
     */
    public Party createParty(final String movieId, final IFeedbackHandler feedback)
    {
        // Create Party
        final Party party = _partyCache.createParty(movieId);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Update the SQL
                boolean result = SQLHelper.createParty(party);

                // Update Firebase
                FirebaseHelper.getInstance().update(party);


                if (feedback != null)
                    feedback.onComplete(result);
            }



        }).start();


        return party;
    }


    /**
     * Update the Party
     * @param partyId
     * @return
     */
    public Party updateParty(final String partyId, final IFeedbackHandler feedback)
    {
        // Update the Cache Object
        final Party party = _partyCache.updateParty(partyId, feedback);


        // Create thread to do extensive stuffs
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Update the SQL
                boolean result = SQLHelper.updateParty(party);

                // Update Firebase (If it's Up. If it's not.... need a way to update it "later")
                // Firebase Helper can store a any "unsynced" changes in its own class

                // Update Firebase
                FirebaseHelper.getInstance().update(party);

                // Handler
                if (feedback != null)
                    feedback.onComplete(result);
            }
        }).start();


        return party;
    }


    /**
     * Delete Party
     * @param partyId
     */
    public void deleteParty(final String partyId, final IFeedbackHandler feedback)
    {
        // Delete Party
        _partyCache.deleteParty(partyId);


        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Delete from SQL
                boolean result = SQLHelper.deleteParty(partyId);

                // Delete from Firebase
                FirebaseHelper.getInstance().delete(partyId);

                // Handler
                if (feedback != null)
                    feedback.onComplete(result);
            }
        }).start();

        return;
    }


    /**
     *
     * @param listener
     */
    public void addPartyListener(IPartyListener listener)
    {
        FirebaseHelper.getInstance().addPartyListener(listener);
    }


    /**
     * Gets the party by ID
     * @param partyId
     * @return
     */
    public Party getParty(String partyId)
    {
        return _partyCache.getParty(partyId);
    }


    /**
     * Gets a party by Movie ID
     * @param movieId
     * @return
     */
    public Collection<Party> getPartyByMovieId(String movieId)
    {
        return _partyCache.getPartyByMovieId(movieId);
    }
}
