package com.spirit.movies.model.cache;

import android.graphics.Bitmap;

import com.spirit.movies.model.LoadImage;

/**
 * Created by spirit on 12/10/2015.
 */
public class CacheImage extends LoadImage
{
    @Override
    public Bitmap handle(String url)
    {
        Bitmap image = CacheImageMap.getInstance().get(url);

        if (image != null)
            return image;

        return super.handle(url);
    }
}
