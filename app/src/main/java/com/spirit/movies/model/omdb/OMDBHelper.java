package com.spirit.movies.model.omdb;

import android.util.Log;

import com.spirit.movies.model.Movie;
import com.spirit.movies.model.http.HTTPHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spirit on 19/09/2015.
 */
public class OMDBHelper
{
    public enum OMDBPlot
    {
        Short,
        Full,
    }

    static public int TIMEOUT = 5000;

    static public String OMDB_URL = "http://www.omdbapi.com/";
    static public String OMDB_MIME = "application/json";

    // JSON Keys
    static final String KEY_SEARCH = "Search";

    static final String KEY_TITLE = "Title";
    static final String KEY_PLOT = "Plot";
    static final String KEY_YEAR = "Year";
    static final String KEY_IMDB = "imdbID";
    static final String KEY_POSTER = "Poster";


    /**
     * Setup the GET URL to retrieve an array of movies
     * @param query
     * @return A String representing the URL with GET parameters
     */
    static public String urlSearchQuery(String query)
    {
        return OMDB_URL + "?s=" + query + "&r=json";
    }

    /**
     * Setup the Get URL to retrieve Movie info from ID, with a short plot
     * @param id
     * @return
     */
    static public String urlMovieInfo(String id)
    {
        //return OMDB_URL + "?i=" + id + "&r=json";
        return urlMovieInfo(id, OMDBPlot.Short);
    }

    /**
     * Setup the Get URL to retrieve movie from ID, with full plot
     * @param id
     * @param plot
     * @return
     */
    static public String urlMovieInfo(String id, OMDBPlot plot)
    {
        String plotLength = "short";

        if (plot == OMDBPlot.Full)
            plotLength = "full";

        return OMDB_URL + "?i=" + id + "&r=json&plot=" + plotLength;
    }






    /**
     * Parse the JSON Result into a list of Movie ID's
     * @param jsonSource
     * @return
     */
    static public List<String> jsonParseSearchResults(String jsonSource)
    {
        Log.v("movies", "OMBD: JSON Result = " + jsonSource);

        // Create Movies Collection
        List<String> movies = new ArrayList<>();

        try
        {
            // Parse JSON Source
            JSONObject json = new JSONObject(jsonSource.toString());

            JSONArray search = json.getJSONArray(OMDBHelper.KEY_SEARCH);

            Log.d("movies", "OMDBHelper: Search results contain " + search.length() + " movies");

            for (int i = 0; i < search.length(); ++i)
            {
                JSONObject result = search.getJSONObject(i);
                String id = result.getString(OMDBHelper.KEY_IMDB);

                movies.add(id);
            }
        }
        catch (Exception e)
        {
            Log.d("movies", e.toString());
            return null;
        }

        return movies;
    }


    /**
     * Parse the JSON Result as a movie
     * @param jsonMovieSource Source for teh JSON Object with only the short plot
     * @param jsonMovieSourceFull Source for hte JSON OBject with the full plot
     * @return
     */
    static public Movie jsonParseMovie(String jsonMovieSource, String jsonMovieSourceFull)
    {
        Log.v("movies", "OMDBHelper: JSON Result = " + jsonMovieSource);

        try
        {
            // Parse JSON Source
            JSONObject jsonShort = new JSONObject(jsonMovieSource.toString());
            JSONObject jsonFull = new JSONObject(jsonMovieSourceFull.toString());

            return jsonMovie(jsonShort, jsonFull);
        }
        catch (Exception e)
        {
            Log.d("movies", e.toString());
        }


        return null;
    }


    /**
     * Query the OMDB Databases for Movie Details
     * @param movieId
     * @return
     */
    static public Movie downloadMovieDetails(String movieId)
    {
        Log.v("movies", "OMDB: Movie '" + movieId + "': Downloading Details");

        String jsonMovie = HTTPHelper.download(OMDBHelper.urlMovieInfo(movieId),
                OMDBHelper.OMDB_MIME, OMDBHelper.TIMEOUT);

        String jsonMovieFull = HTTPHelper.download(
                OMDBHelper.urlMovieInfo(movieId, OMDBHelper.OMDBPlot.Full),
                OMDBHelper.OMDB_MIME, OMDBHelper.TIMEOUT);

        // Parse the JSON Source as a movie
        Movie movie = OMDBHelper.jsonParseMovie(jsonMovie, jsonMovieFull);

        if (movie != null)
            Log.i("movies", "OMDB: Movie '" + movieId + "': Details Downloaded");
        else
            Log.i("movies", "OMDB: Movie '" + movieId + "': Error Downloading Details");

        return movie;
    }


    /**
     *
     * @param jsonMovieShort
     * @param jsonMovieFull
     * @return
     */
    static protected Movie jsonMovie(JSONObject jsonMovieShort, JSONObject jsonMovieFull)
    {
        String title;
        String year;
        String id;
        String summary;
        String plot;
        String poster;

        try
        {
            // Movie Information
            id = jsonMovieShort.getString(OMDBHelper.KEY_IMDB);
            title = jsonMovieShort.getString(OMDBHelper.KEY_TITLE);
            year = jsonMovieShort.getString(OMDBHelper.KEY_YEAR);
            summary = jsonMovieShort.optString(OMDBHelper.KEY_PLOT);
            poster = jsonMovieShort.optString(OMDBHelper.KEY_POSTER);

            // Extended Plot
            plot = jsonMovieFull.optString(OMDBHelper.KEY_PLOT);
        }
        catch (Exception e)
        {
            Log.d("movies", "OMDBHelper: Unable to Parse JSON Object to a Movie");
            return null;
        }


        // Create Movie Object
        Movie movie = new Movie(id, title, year, summary, plot, poster, 0.0f);
        return movie;
    }



    /**
     * Hide the Constructor
     */
    private OMDBHelper()
    {

    }
}
