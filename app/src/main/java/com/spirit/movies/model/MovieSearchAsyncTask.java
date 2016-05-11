package com.spirit.movies.model;

import android.os.AsyncTask;

import com.spirit.movies.model.cache.CacheDetail;
import com.spirit.movies.model.omdb.OMDBDetail;
import com.spirit.movies.model.omdb.OMDBSearch;
import com.spirit.movies.model.sql.SQLDetail;
import com.spirit.movies.model.sql.SQLSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spirit on 19/09/2015.
 */
public class MovieSearchAsyncTask extends AsyncTask<String, Integer, List<Movie>>
{
    private IMovieSearchHandler _search = null;

    /**
     * Constructor
     * @param searchHandler
     */
    public MovieSearchAsyncTask(IMovieSearchHandler searchHandler)
    {
        _search = searchHandler;
    }


    /**
     *
     * @param params
     * @return
     */
    @Override
    protected List<Movie> doInBackground(String... params)
    {
        // Search for Movie via Chain of Responsibility

        // Validation : Param List, etc ?

        // Setup Search
        String searchMovie = params[0];

        // Setup Chain for searching
        MovieSearch search = new MovieSearch();
        search.add(new OMDBSearch());   // Search Online DB
        search.add(new SQLSearch());    // Search Local

        // Search
        List<String> ids = search.handle(searchMovie);

        if (ids == null)
            return null;

        // Setup Chain for pulling an individual movie
        MovieDetail detail = new MovieDetail();
        detail.add(new CacheDetail());      // Get Movie Info From Cache
        detail.add(new SQLDetail());        // Get Movie Info From DB
        detail.add(new OMDBDetail());       // Get Movie Info From Interwebs

        // Create Movie List
        List<Movie> movies = new ArrayList<>();

        // Pull Individual Movies from the Appropriate Source
        for (String id : ids)
        {
            // Get Movie Information
            Movie movie = detail.handle(id);

            // Add Movie to List
            movies.add(movie);
        }

        return movies;
    }


    /**
     *
     * @param movies
     */
    @Override
    protected void onPostExecute(List<Movie> movies)
    {
        if (_search != null)
            _search.searchResults(movies);
    }
}
