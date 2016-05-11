package com.spirit.movies.model.sql;

import com.spirit.movies.model.Movie;
import com.spirit.movies.model.MovieDetail;

/**
 * Created by spirit on 19/09/2015.
 */
public class SQLDetail extends MovieDetail
{
    /**
     *
     * @param id
     * @return
     */
    @Override
    public Movie handle(String id)
    {
        // Search for Movie
        Movie movie = SQLHelper.getMovie(id);

        // Movie Exists ?
        if (movie != null)
        {
            return movie;
        }

        // Next in the Chain
        return super.handle(id);
    }
}
