package com.spirit.movies.controller;


import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.spirit.movies.R;
import com.spirit.movies.model.IMovieDetailHandler;
import com.spirit.movies.model.LoadImageAsyncTask;
import com.spirit.movies.model.Movie;
import com.spirit.movies.model.MovieDetailAsyncTask;
import com.spirit.movies.model.MovieFacade;
import com.spirit.movies.model.Party;
import com.spirit.movies.model.sql.IFeedbackHandler;

import java.util.Collection;


/**
 * Created by spirit on 4/08/2015.
 */
public class MovieDetailActivity extends ActionBarActivity
{
    private Movie _movie;

    /**
     *
     */
    public MovieDetailActivity()
    {


    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        // Get the ID from the parameters passed to the intent
        String movie_id = getIntent().getExtras().get("movie").toString();

        // Get & Display Movie Details
        setDetails(movie_id);


        // Setup Party List : Adapter is setup in onResume()
        ListView lv = (ListView)findViewById(R.id.movie_display_party_list);

        // Set Event Listener when clicking on item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Setup Navigation Intent
                Intent intent = new Intent(getApplicationContext(), PartyDetailActivity.class);

                // Get PartyID
                Party party = (Party)parent.getAdapter().getItem(position);

                intent.putExtra("action", "edit");
                intent.putExtra("party", party.getId());

                startActivity(intent);
            }
        });

        // Get Rating Control
        RatingBar rating = (RatingBar)findViewById(R.id.movie_display_rating);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, float rating, boolean fromUser)
            {
                if (fromUser == false)
                    return;

                // Update Rating
                MovieFacade.getInstance().updateMovieRating(_movie.getId(), rating, new IFeedbackHandler()
                {
                    @Override
                    public void onComplete(boolean success)
                    {
                        Toaster.displayToast(ratingBar, R.string.feedback_rating_update, Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    /**
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();

        setPartyDetails();
    }


    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_listing, menu);
        return true;
    }


    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.movie_action_create_party)
        {
            createParty();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Retrieve Movie Details, and display fields
     * @param movie_id
     */
    private void setDetails(String movie_id)
    {
        IMovieDetailHandler handler = new IMovieDetailHandler()
        {
            @Override
            public void detailResults(Movie movie)
            {
                _movie = movie;

                if (_movie == null)
                    Log.d("movies", "MOVIE: Movie Not Found");

                TextView title = (TextView) findViewById(R.id.movie_display_title);
             //   ImageView poster = (ImageView)findViewById(R.id.movie_display_poster);
                TextView plot = (TextView)findViewById(R.id.movie_display_plot);
                RatingBar rating = (RatingBar)findViewById(R.id.movie_display_rating);


                // Set Data
                if (_movie != null)
                {
                    title.setText(_movie.getTitle());
                    plot.setText(_movie.getPlot());

                    rating.setRating(_movie.getRating());

                    // Set Poster Image
                    setPosterImage(_movie.getPosterUrl());

                    // Set Party Details
                    setPartyDetails();
                }
            }
        };

        MovieDetailAsyncTask task = new MovieDetailAsyncTask(handler);
        task.execute(movie_id);
    }


    /**
     *
     */
    private void setPartyDetails()
    {
        if (_movie == null)
            return;

        // Setup Party List
        Collection<Party> parties = MovieFacade.getInstance().getPartyByMovieId(_movie.getId());

        PartyItemAdapter adapter = new PartyItemAdapter(this, parties);
        ListView lv = (ListView)findViewById(R.id.movie_display_party_list);
        lv.setAdapter(adapter);
    }


    /**
     *
     */
    private void createParty()
    {
        System.out.println("create");

        // Setup Navigation Intent
        Intent intent = new Intent(getApplicationContext(), PartyDetailActivity.class);

        // Put Movie ID
        intent.putExtra("action", "create");
        intent.putExtra("movie", _movie.getId());

        // Start Activity
        startActivity(intent);
    }


    /**
     * Sets the Image
     * @param url
     */
    private void setPosterImage(String url)
    {
        // Set the Poster Image
        LoadImageAsyncTask task = new LoadImageAsyncTask()
        {
            @Override
            protected void onPostExecute(Bitmap bitmap)
            {
                ImageView poster = (ImageView)findViewById(R.id.movie_display_poster);
                poster.setImageBitmap(bitmap);
            }
        };

        // Execute Task
        task.execute(url);
    }
}
