package com.spirit.movies.model.cache;

import android.graphics.Bitmap;
import android.util.Log;

import com.spirit.movies.util.LRUCacheMap;

/**
 * Created by spirit on 12/10/2015.
 */
public class CacheImageMap
{
    static private CacheImageMap _instance;
    static public CacheImageMap getInstance()
    {
        if (_instance == null)
            _instance = new CacheImageMap();

        return _instance;
    }



    static private final int CACHE_SIZE = 10;

    private LRUCacheMap<String, Bitmap> _cache;

    /**
     *
     */
    private CacheImageMap()
    {
        _cache = new LRUCacheMap<>(CACHE_SIZE);
    }


    /**
     *
     * @param url
     * @return
     */
    public boolean containsKey(String url)
    {
        return _cache.containsKey(url);
    }

    /**
     *
     * @param url
     * @param bitmap
     */
    public boolean put(String url, Bitmap bitmap)
    {
        _cache.put(url, bitmap);

        if (bitmap == null)
            return false;

        // Only put in map IFF it's not already in their
        if (containsKey(url))
            return true;

        // Put movie in the map
        _cache.put(url, bitmap);

        Log.d("movies", "CACHE: Image '" + url + "' cached");

        // Return Whether the movie is in the map or not
        return containsKey(url);

    }

    /**
     *
     * @param url
     * @return
     */
    public Bitmap get(String url)
    {
        // Get Movie from Map
        Bitmap bitmap = _cache.get(url);

        // Debug
        if (bitmap != null)
            Log.d("movies", "CACHE: Image '" + url + "' found");
        else
            Log.d("movies", "CACHE: Image '" + url + "' not found");

        return bitmap;
    }

}
