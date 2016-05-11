package com.spirit.movies.model;

/**
 * Created by spirit on 2/08/2015.
 */
public class Movie
{
    private String _id;

    private String _title;
    private String _year;

    private String _shortPlot;
    private String _longPlot;
    private String _posterUrl;

    private float _rating = 0.0f;

    /**
     * Get OMDBHelper ID
     * @return
     */
    public String getId() {return _id;}

    /**
     * Get Movie Title
     * @return
     */
    public String getTitle() {return _title;}
    public String getYear() {return _year;}

    /**
     * Get Summary of Movie
     * @return
     */
    public String getSummary() {return _shortPlot;}

    /**
     * Get Plot
     * @return
     */
    public String getPlot() {return _longPlot == null ? _shortPlot : _longPlot;}

    /**
     * Get Poster URL
     * @return
     */
    public String getPosterUrl() {return _posterUrl;}

    /**
     * Get User Rating of Movie
     * @return
     */
    public float getRating() {return _rating;}

    /**
     * Set User Rating of Movie
     * @param rating
     */
    public void setRating(float rating)
    {
        // Set Local / Cache Rating
        _rating = rating;
    }


    /**
     * @deprecated
     */
 //   private Party _party;

    /**
     * Get Party
     * @return
     */
  //  public Party getParty() {return _party;}


    /**
     * Creates the Movie
     * @param id
     * @param title
     * @param year
     * @param summary
     * @param plot
     * @param posterUrl
     */
    public Movie(String id, String title, String year, String summary, String plot, String posterUrl, float rating)
    {
        _id = id;

        _title = title;
        _year = year;

        _posterUrl = posterUrl;

        _shortPlot = summary;
        _longPlot = plot;

        _rating = rating;
    }
}
