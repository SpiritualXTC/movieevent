package com.spirit.movies.model.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by unfor on 20/09/2015.
 */
public class HTTPHelper
{
    /**
     * This is really generic, so could be used as a helper for any form of request :)
     * @param urlGET
     * @param timeout
     * @return
     */
    static public String download(String urlGET, String mime, int timeout)
    {
        try
        {
            Log.v("movies", "URL: URL = " + urlGET);

            // Setup URL
            URL url = new URL(urlGET);

            // Setup Connection
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", mime);

            connection.setReadTimeout(timeout);
            connection.setConnectTimeout(timeout);

            // Status
            int statusCode = 0;

            try
            {
                statusCode = connection.getResponseCode();
            }
            catch(IOException e)
            {
                Log.d("movies", e.toString());
            }

            // Check Response Code
            if (statusCode != HttpURLConnection.HTTP_OK)
            {
                Log.d("movies", "HTTP: Invalid Response Code");
                return null;
            }

            // Get Contents of Stream
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));


            // Progress Update
            int length = connection.getContentLength();

            Log.v("movies", "HTTP: Content Length = " + length);

            StringBuilder sb = new StringBuilder();
            String line = "";
            int readCurrent = 0;
            while ((line = reader.readLine()) != null)
            {
                // log individual line
                //Log.i(LOG_TAG, line);
                sb.append(line);
                readCurrent += line.length();

                Log.v("movies", "HTTP: Progress = " + readCurrent + " / " + length);
            }

            return sb.toString();
        }
        catch (Exception e)
        {
            Log.d("movies", "HTTP: Failed to download content");
            Log.d("movies", "Exception = " + e.toString());
        }

        return "";
    }

}
