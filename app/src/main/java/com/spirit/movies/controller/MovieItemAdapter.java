package com.spirit.movies.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.spirit.movies.R;
import com.spirit.movies.model.LoadImageAsyncTask;
import com.spirit.movies.model.Movie;
import com.spirit.movies.model.MovieFacade;
import com.spirit.movies.model.Party;
import com.spirit.movies.model.sql.IFeedbackHandler;

import java.util.Collection;
import java.util.List;

/**
 * Created by s3435406 on 4/08/2015.
 */
public class MovieItemAdapter extends ArrayAdapter<Movie>
{
    private Context _context;
    private List<Movie> _items;

    /**
     * Create the Adapter for a list
     * @param c
     * @param items
     */
    public MovieItemAdapter(Context c, List<Movie> items)
    {
        super(c, R.layout.movie_listitem, android.R.id.text1, items);

        _context = c;
        _items = items;
    }


    /**
     * Get the List Item View
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.movie_listitem, parent, false);

        view.setFocusable(false);

        // Get Movie
//        final Movie movie = MovieFacade.getInstance().getMovie(id);
        final Movie movie = _items.get(position);


        // Setup Title
        TextView tv = (TextView) view.findViewById(R.id.movie_title_view);
        tv.setFocusable(false);

        // Setup Year
        TextView yv = (TextView) view.findViewById(R.id.year_view);
        yv.setFocusable(false);

        // Setup Short Plot
        TextView sv = (TextView) view.findViewById((R.id.summary_view));
        sv.setFocusable(false);

        // Setup Rating Bar
        RatingBar rb = (RatingBar) view.findViewById((R.id.movie_rating));
        rb.setFocusable(false);

        // Change the Rating of the Movie
        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, float rating, boolean fromUser)
            {
                if (fromUser == false)
                {
                    Log.d("movie", "eating rating event: " + rating);
                    return;
                }
                Log.d("movie", "rating change: " + rating);

                // Prevent Bug <,< LOL
                movie.setRating(rating);

                // Update Rating
                MovieFacade.getInstance().updateMovieRating(movie.getId(), rating, new IFeedbackHandler()
                {
                    @Override
                    public void onComplete(boolean success)
                    {
                        Toaster.displayToast(ratingBar, R.string.feedback_rating_update, Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        // Movie Valid ?
        if (movie != null)
        {
            // Set Fields
            tv.setText(movie.getTitle());
            yv.setText(movie.getYear());
            sv.setText(movie.getSummary());
            rb.setRating(movie.getRating());


            // Set the Poster Image
            if (movie.getPosterUrl() != null)
            {
                LoadImageAsyncTask task = new LoadImageAsyncTask()
                {
                    @Override
                    protected void onPostExecute(Bitmap bitmap)
                    {
                        ImageView iv = (ImageView) view.findViewById(R.id.poster_thumb_view);
                        iv.setImageBitmap(bitmap);

                        // Cache the Image
                        MovieFacade.getInstance().cacheImage(movie.getPosterUrl(), bitmap);
                    }
                };

                task.execute(movie.getPosterUrl());
            }

            Collection<Party> parties = MovieFacade.getInstance().getPartyByMovieId(movie.getId());

            TextView invited = (TextView) view.findViewById(R.id.movie_party_count);
            invited.setText("" + parties.size());
        }
        else
        {
            Log.d("movies", "No Movie matching that ID stored in local cache");
            tv.setText("invalid movie");
        }

        return view;
    }



}
