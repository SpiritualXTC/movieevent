package com.spirit.movies.model.cache;

import android.util.Log;

import com.spirit.movies.model.Movie;
import com.spirit.movies.util.LRUCacheMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by spirit on 19/09/2015.
 */
public class CacheMovieHelper
{
    static private CacheMovieHelper _instance = new CacheMovieHelper();
    static public CacheMovieHelper getInstance()
    {
        return _instance;
    }



    static private final int CACHE_SIZE = 2;

    private LRUCacheMap<String, Movie> _map;

    public int Size() { return _map.size();}


    /**
     *
     */
    private CacheMovieHelper()
    {
        _map = new LRUCacheMap<>(CACHE_SIZE, 0.75f);
    }


    /**
     * Determines whether the movie is cached
     * @param movie_id
     * @return
     */
    public boolean hasMovie(String movie_id)
    {
        return _map.containsKey(movie_id);
    }

    /**
     * Cache the Movie
     * @param movie
     * @return
     */
    public boolean cacheMovie(Movie movie)
    {
        if (movie == null)
            return false;

        // Only put in map IFF it's not already in their
        if (hasMovie(movie.getId()))
            return true;

        // Put movie in the map
        _map.put(movie.getId(), movie);

        Log.d("movies", "CACHE: Movie '" + movie.getId() + "' cached");

        // Return Whether the movie is in the map or not
        return hasMovie(movie.getId());
    }

    /**
     * Retrieves the Movie from the Cache
     * @param movie_id
     * @return
     */
    public Movie getMovie(String movie_id)
    {
        // Get Movie from Map
        Movie movie = _map.get(movie_id);

        // Debug
        if (movie != null)
            Log.d("movies", "CACHE: Movie '" + movie_id + "' found");
        else
            Log.d("movies", "CACHE: Movie '" + movie_id + "' not found");

        return movie;
    }
}
