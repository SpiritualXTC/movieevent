package com.spirit.movies.model.omdb;

import android.util.Log;

import com.spirit.movies.model.Movie;
import com.spirit.movies.model.MovieDetail;
import com.spirit.movies.model.MovieFacade;
import com.spirit.movies.model.http.HTTPHelper;
import com.spirit.movies.model.sql.SQLHelper;

/**
 * Created by spirit on 19/09/2015.
 */
public class OMDBDetail extends MovieDetail
{
    /**
     * Get Movie Details from OMDBHelper
     * @param id
     * @return
     */
    public Movie handle(String id)
    {
        // Online Check
        if (MovieFacade.getInstance().isConnected())
        {
            // Download Info from OMDBHelper
            Movie movie = OMDBHelper.downloadMovieDetails(id);

            // Valid ?
            if (movie != null)
            {
                // Store the movie
                MovieFacade.getInstance().storeMovie(movie);

                return movie;
            }
        }
        else
            Log.d("movies", "OMDB: No Connectivity");

        return super.handle(id);
    }
}
