package com.spirit.movies.controller;

import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.AccountPicker;
import com.spirit.movies.R;
import com.spirit.movies.model.IConnectionChange;
import com.spirit.movies.model.IMovieDetailHandler;
import com.spirit.movies.model.IMovieSearchHandler;
import com.spirit.movies.model.IPartyListener;
import com.spirit.movies.model.Movie;
import com.spirit.movies.model.MovieDetailAsyncTask;
import com.spirit.movies.model.MovieFacade;
import com.spirit.movies.model.MovieSearchAsyncTask;
import com.spirit.movies.model.NetworkConnectionManager;
import com.spirit.movies.model.Party;
import com.spirit.movies.model.sql.SQLHelper;
import com.spirit.movies.util.ConvertUtil;
import com.spirit.movies.util.UserAccount;

import java.util.Collections;
import java.util.List;


/**
 * Created by spirit on 4/08/2015.
 */
public class MovieActivity extends ActionBarActivity
{
    private static int ACCOUNT_REQUEST_CODE = 1;

    private Menu _menu;

    private MovieItemAdapter _movieAdapter = null;

    private SQLHelper _sql = null;




    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get User ID
        String id = UserAccount.getUserId(this);

        if (id == null)
        {
            Log.d("movies", "No User ID. Prompting");

            Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                    false, null, null, null, null);
            startActivityForResult(intent, ACCOUNT_REQUEST_CODE);
        }


        // Initialise the model
        MovieFacade.getInstance().initialise(this, id);
        MovieFacade.getInstance().addPartyListener(new IPartyListener()
        {
            @Override
            public void onPartyInvite(String id)
            {
                Log.d("movies", "IT'S A PARTY!!");


                Notification notification = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(android.R.drawable.ic_notification_overlay)
                        .setContentText("Party Invite")
                        .setContentTitle("Party!").build();

                NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mgr.notify(1, notification);
            }

            @Override
            public void onPartyUpdate(Party party)
            {
                displayPartyUpdateNotification(party);
            }
        });


        /**
         * Setup listener for connection change
         */
        NetworkConnectionManager.getInstance().addListener(new IConnectionChange()
        {
            @Override
            public void onConnectionChange(boolean connected)
            {
                if (connected)
                    redoSearchMovies();
            }
        });

    }


    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Gain Access to the menu
        _menu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        Log.d("movies", "Setting UP Search");

        // Get Search Option
        final SearchView search = (SearchView)menu.findItem(R.id.menu_movie_search).getActionView();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchMovies(query);

                search.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String query)
            {
//                Log.d("movies", "search change = " + query);
                // Search Local Database for queries and show in drop down ?
                // Or Do Nothing :)
                return true;
            }
        });

        return true;
    }

    /**
     * Resume the Activity
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        // Notify the Adapter to update the list
        if (_movieAdapter != null)
            _movieAdapter.notifyDataSetChanged();
    }

    /**
     * Pause the Activity
     */
    @Override
    protected void onPause()
    {
        super.onPause();

        // Close Database Connection
        SQLHelper.close();
    }



    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Result when selecting an account.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (requestCode == ACCOUNT_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                Log.d("movies", "User Email = " + email);
                MovieFacade.getInstance().setUser(this, email);
            }
            else
                Log.d("movies", "Error Selecting Account");
        }
    }



    /**
     * Creates the Adapter
     */
    protected void createAdapter(List<Movie> movies)
    {
        ListView lv = (ListView)findViewById(R.id.movielist);

        // Create Adapter
        _movieAdapter = new MovieItemAdapter(this, movies);
        lv.setAdapter(_movieAdapter);

        // Set Event Listener when clicking on item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Setup Navigation Intent
                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);

                // Get Movie ID
                Movie movie = _movieAdapter.getItem(position);
                String movie_id = movie.getId();

                // Cache the Movie
                boolean result = MovieFacade.getInstance().cacheMovie(movie);

                if (! result)
                    Log.e("movies", "Failed to Cache");

                //Log.d("movies", "Movie ID = " + movie_id);

                intent.putExtra("movie", movie_id);

                // Start Activity
                startActivity(intent);
            }
        });
    }


    /**
     * Search for a list of movies from the given search parameter
     * @param search
     */
    private void searchMovies(String search)
    {
        Log.d("movies", "SEARCH: Query = " + search);

        // Setup the Task and Handler
        MovieSearchAsyncTask searchTask = new MovieSearchAsyncTask(new IMovieSearchHandler()
        {
            @Override
            public void searchResults(List<Movie> movies)
            {
                if (movies == null)
                {
                    Log.d("movies", "SEARCH: Invalid Result");
                    return;
                }

                createAdapter(Collections.unmodifiableList(movies));
            }
        });

        // Execute Task
        searchTask.execute(search);
    }


    /**
     * Resends the search request for movies
     */
    private void redoSearchMovies()
    {
        if (_menu == null)
            return;

        // Get Search Bar
        SearchView search = (SearchView)_menu.findItem(R.id.menu_movie_search).getActionView();

        if (search == null)
            return;

        // Get Search Query
        CharSequence query = search.getQuery();

        // Is their a search present?
        if (query != null && ! query.equals(""))
        {
            // Execute Search
            searchMovies(query.toString());
        }
    }


    /**
     *
     * @param party
     */
    private void displayPartyUpdateNotification(final Party party)
    {
        new MovieDetailAsyncTask(new IMovieDetailHandler()
        {
            @Override
            public void detailResults(Movie movie)
            {
                String date = "Date: " + ConvertUtil.convertDateString(MovieActivity.this, party.getDateTime());
                String time = "Time: " + ConvertUtil.convertTimeString(MovieActivity.this, party.getDateTime());

                Notification notification= new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(android.R.drawable.ic_notification_overlay)
                        .setContentTitle("Party @ '" + party.getVenue() + "' updated")
                        .setContentText(movie.getTitle())
                        .setSubText(date + " - " + time)
                        .build();

                NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                mgr.notify(1, notification);
            }
        }).execute(party.getMovieId());
    }
}



