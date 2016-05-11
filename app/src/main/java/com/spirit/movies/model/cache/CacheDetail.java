package com.spirit.movies.model.cache;

import com.spirit.movies.model.Movie;
import com.spirit.movies.model.MovieDetail;

/**
 * Created by spirit on 19/09/2015.
 */
public class CacheDetail extends MovieDetail
{
    /**
     * Gets the movie from local memory if it exists
     * @param movie_id
     * @return
     */
    @Override
    public Movie handle(String movie_id)
    {
        // Get Movie From Cache
        Movie movie = CacheMovieHelper.getInstance().getMovie(movie_id);
        if (movie != null)
            return movie;

        return super.handle(movie_id);
    }
}
