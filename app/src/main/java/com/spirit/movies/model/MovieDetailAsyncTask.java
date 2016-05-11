package com.spirit.movies.model;

import android.os.AsyncTask;
import android.util.Log;

import com.spirit.movies.model.cache.CacheDetail;
import com.spirit.movies.model.omdb.OMDBDetail;
import com.spirit.movies.model.sql.SQLDetail;


/**
 * Created by spirit on 19/09/2015.
 */
public class MovieDetailAsyncTask extends AsyncTask<String, Integer, Movie>
{
    private IMovieDetailHandler _detailHandler;

    /**
     *
     * @param handler
     */
    public MovieDetailAsyncTask(IMovieDetailHandler handler)
    {
        _detailHandler = handler;
    }


    /**
     *
     * @param params
     * @return
     */
    @Override
    protected Movie doInBackground(String... params)
    {
        // Use Chain of Responsibility to load movie details
        String id = params[0];

        Log.d("movies", "DETAIL: Retrieve Details for '" + id + "'");

        MovieDetail detail = new MovieDetail();
        detail.add(new CacheDetail());          // Search in Memory
        detail.add(new SQLDetail());            // Search in SQL Database.
        detail.add(new OMDBDetail());           // Search OMDB Online Database

        // Get Movie Details
        Movie movie = detail.handle(id);

        return movie;
    }


    /**
     *
     * @param movie
     */
    @Override
    protected void onPostExecute(Movie movie)
    {
        if (_detailHandler != null)
            _detailHandler.detailResults(movie);
    }
}
