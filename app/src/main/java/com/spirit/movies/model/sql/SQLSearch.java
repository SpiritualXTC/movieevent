package com.spirit.movies.model.sql;

import com.spirit.movies.model.MovieSearch;

import java.util.List;

/**
 * Created by spirit on 19/09/2015.
 */
public class SQLSearch extends MovieSearch
{
    @Override
    public List<String> handle(String search)
    {
        List<String> movies = SQLHelper.searchMovies(search);

        if (movies != null)
            return movies;

        return super.handle(search);
    }
}
