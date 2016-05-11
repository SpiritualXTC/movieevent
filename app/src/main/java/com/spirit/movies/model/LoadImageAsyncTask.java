package com.spirit.movies.model;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.spirit.movies.model.cache.CacheImage;
import com.spirit.movies.model.http.DownloadImage;

/**
 * Created by spirit on 20/09/2015.
 */
public abstract class LoadImageAsyncTask extends AsyncTask<String, Integer, Bitmap>
{

    /**
     *
     */
    public LoadImageAsyncTask()
    {

    }


    /**
     *
     * @param params
     * @return
     */
    @Override
    protected Bitmap doInBackground(String... params)
    {
        // Validation
        String url = params[0];

        // Setup Chain
        LoadImage loader = new LoadImage();

        // Load from SQL / Memory
        loader.add(new CacheImage());
        loader.add(new DownloadImage());

        Bitmap image = loader.handle(url);

        return image;
    }

    /**
     *
     * @param bmp
     */
//    @Override
//    protected void onPostExecute(Bitmap bmp)
//    {
//        if (_imageHandler != null)
//            _imageHandler.imageResult(bmp);
//    }

}
