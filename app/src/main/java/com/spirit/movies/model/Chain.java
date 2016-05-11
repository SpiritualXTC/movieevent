package com.spirit.movies.model;

/**
 * Chain of Responsibility Generic Base Class
 * Created by spirit on 19/09/2015.
 */
public class Chain <PARAM,RETURN>
{
    private Chain<PARAM, RETURN> _next;


    /**
     *
     */
    public Chain()
    {

    }


    /**
     *
     * @param next
     * @return
     */
    final public void add(Chain<PARAM, RETURN> next)
    {
        if (_next != null)
            _next.add(next);
        else
            _next = next;

    }


    /**
     *
     * @return
     */
    public RETURN handle(PARAM arg)
    {
        if (_next != null)
            return _next.handle(arg);
        else
            return null;
    }
}
