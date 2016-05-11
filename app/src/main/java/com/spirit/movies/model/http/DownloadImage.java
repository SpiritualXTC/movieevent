package com.spirit.movies.model.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.spirit.movies.model.LoadImage;

import java.io.InputStream;

/**
 * Created by unfor on 20/09/2015.
 */
public class DownloadImage extends LoadImage
{
    @Override
    public Bitmap handle(String url)
    {
        Bitmap image;
        try
        {
            InputStream in = new java.net.URL(url).openStream();
            image = BitmapFactory.decodeStream(in);

            return image;
        }
        catch (Exception e)
        {
            Log.e("movies", e.getMessage());
            e.printStackTrace();
        }

        return super.handle(url);
    }

}
