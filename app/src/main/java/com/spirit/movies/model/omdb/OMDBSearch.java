package com.spirit.movies.model.omdb;

import android.util.Log;

import com.spirit.movies.model.MovieFacade;
import com.spirit.movies.model.MovieSearch;
import com.spirit.movies.model.http.HTTPHelper;

import java.util.List;

/**
 * Created by spirit on 19/09/2015.
 */
public class OMDBSearch extends MovieSearch
{
    /**
     *
     */
    public OMDBSearch()
    {

    }


    /**
     *
     * @param search
     * @return
     */
    @Override
    public List<String> handle(String search)
    {
        // If connected...
        if (MovieFacade.getInstance().isConnected())
        {
            // Download the search results from OMDBHelper Rest API
            String json = HTTPHelper.download(OMDBHelper.urlSearchQuery(search),
                    OMDBHelper.OMDB_MIME, OMDBHelper.TIMEOUT);

            // Get IDs from List
            List<String> id_list = OMDBHelper.jsonParseSearchResults(json);

            // No List ? Invalid Search
            if (id_list != null)
                return id_list;
            else
                Log.d("movies", "OMDBHelper: No Result");
        }
        else
            Log.d("movies", "OMDB: No Connectivity");

        // Pass to next in chain
        return super.handle(search);
    }
}
