package com.spirit.movies.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by spirit on 2/08/2015.
 */
public class Party
{
    private String _partyId;
    private String _movieId;

    private String _venue;
    private String _geoLocation;            //String representation of latitude/longitude
    private ArrayList<String> _invited;
    private Calendar _calendar;

    public String getId() {return _partyId;}
    public String getMovieId() {return _movieId;}

    /**
     * Get Date/Time Object
     * @return
     */
    public Date getDateTime() {return _calendar.getTime();}

    /**
     * Get Venue
     * @return
     */
    public String getVenue() {return _venue;}

    /**
     * Get GeoLocation
     * @return
     */
    public String getLocation() {return _geoLocation;}

    /**
     * Get Invited List
     * @return
     */
    public ArrayList<String> getInvited() {return _invited;}

    /**
     * Gets a reference to the calender
     * @return
     */
    public Calendar getCalendar() {return _calendar;}

    /**
     * Sets the Venue for theParty
     * @param s
     */
    public void setVenue(String s) {_venue = s;}

    /**
     * Sets the GeoLocation [long],[lat]
     * @param s
     */
    public void setLocation(String s) {_geoLocation = s;}


    /**
     * Sets the Date & Time
     * @param date
     */
    public void setDate(Date date)
    {
        _calendar.setTime(date);
    }

    /**
     *
     * @param id
     * @param movieId
     */
    public Party(String id, String movieId)
    {
        _partyId = id;
        _movieId = movieId;

        // Get Instance of a calender
        _calendar = Calendar.getInstance();

        _venue = "";
        _geoLocation = "";

        _invited = new ArrayList<>();
    }


    /**
     * <Copy Constructor>
     * Make a complete copy of the original object
     * @param copy
     */
    public Party(Party copy)
    {
        if (copy == null)
            throw new IllegalArgumentException();

        _partyId = copy.getId();
        _movieId = copy.getMovieId();

        _venue = copy.getVenue();
        _geoLocation = copy.getLocation();

        _calendar = Calendar.getInstance();
        _calendar.setTime(copy.getDateTime());

        // Copy Invited List
        _invited = new ArrayList<>();

        for (String s : copy.getInvited())
            _invited.add(s);
    }
}
