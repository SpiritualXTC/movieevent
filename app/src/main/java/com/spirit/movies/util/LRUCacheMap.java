package com.spirit.movies.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by spirit on 11/10/2015.
 */
public class LRUCacheMap<K, V> extends LinkedHashMap<K, V>
{
    private int _cacheLimit = 10;

    /**
     *
     */
    public LRUCacheMap()
    {
        this(100);
    }

    /**
     *
     * @param cacheLimit
     */
    public LRUCacheMap(int cacheLimit)
    {
        this(cacheLimit, 0.75f);
    }

    /**
     *
     * @param cacheLimit
     * @param loadFactor
     */
    public LRUCacheMap(int cacheLimit, float loadFactor)
    {
        super(cacheLimit, loadFactor, true);
        _cacheLimit = cacheLimit;
    }

    /**
     * Fired during a put().
     * @param eldest
     * @return
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
    {
        // When to remove the eldest entry.
        // Excess Size
        return size() > _cacheLimit;
    }
}
