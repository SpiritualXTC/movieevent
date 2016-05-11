package com.spirit.movies.model.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.spirit.movies.model.Movie;
import com.spirit.movies.model.Party;
import com.spirit.movies.util.ConvertUtil;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * NOTE: Make the DB connection persist across the lifecycle of the app :)
 *
 * Created by spirit on 19/09/2015.
 */
public class SQLHelper
{
    static final String MOVIE_DB_NAME = "movies_db";
    static final String MOVIE_TABLE_NAME = "movies";
    static final String PARTY_TABLE_NAME = "parties";
    static final String INVITATION_TABLE_NAME = "invitations";

    static private Context _context;
    static private SQLiteDatabase _database;

    /**
     * Connect to the SQLLite Database
     */
    static public void initialise(Context c)
    {
        _context = c;

        // Make Connection to Database in an different Thread
        // Move all Database Functionality to a separate "helper" class

        // Get Instance to Database Connection
        SQLiteDatabase db = connect();

        // Drop Movie Table
//                db.execSQL("DROP TABLE movies");
//                db.execSQL("DROP TABLE invitations");
//                db.execSQL("DROP TABLE parties");



        // Create Movie Cache Table!
        String sql_create_movies = "CREATE TABLE IF NOT EXISTS ";   // Conditional Create
        sql_create_movies += MOVIE_TABLE_NAME + " (";               // Table Name
        sql_create_movies += "movie_id VARCHAR(24) NOT NULL,";      // OMDBHelper Database ID
        sql_create_movies += "movie_title VARCHAR(128),";           // Movie Title
        sql_create_movies += "movie_year VARCHAR(24),";             // Movie Year
        sql_create_movies += "movie_summary TEXT,";                 // Summary
        sql_create_movies += "movie_plot TEXT,";                    // Plot
        sql_create_movies += "movie_poster VARCHAR(128),";          // Poster Image
        sql_create_movies += "movie_rating DECIMAL,";               // User Rating (local DB :))
        sql_create_movies += "PRIMARY KEY(movie_id))";              // Set Primary Key

        db.execSQL(sql_create_movies);


//                String s = "ALTER TABLE movies MODIFY COLUMN movie_title VARCHAR(128) CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI";
//                db.execSQL(s);


        // Create Party Cache Table!
        String sql_create_party = "CREATE TABLE IF NOT EXISTS ";    // Conditional Create
        sql_create_party += PARTY_TABLE_NAME + " (";                // Table Name
        sql_create_party += "party_id VARCHAR(48) NOT NULL, ";      // Party ID"
        sql_create_party += "party_movie_id VARCHAR(24) NOT NULL, ";// Movie ID
        sql_create_party += "party_date DATETIME NOT NULL, ";       // Datetime
        sql_create_party += "party_venue TEXT, ";                   // Venue
        sql_create_party += "party_geolocation TEXT, ";             // Geolocation
        sql_create_party += "PRIMARY KEY(party_id))";               // Set Primary Key

        db.execSQL(sql_create_party);

        // Create Party Invitation Cache Table! [Could the party table just store JSON ???] lol
        String sql_create_invitation = "CREATE TABLE IF NOT EXISTS ";   // Conditional Create
        sql_create_invitation += INVITATION_TABLE_NAME + " (";          // Table Name
        sql_create_invitation += "party_id VARCHAR(24) NOT NULL, ";     // Party ID
        sql_create_invitation += "invite_email VARCHAR(128) NOT NULL, ";// Email
        sql_create_invitation += "PRIMARY KEY(party_id, invite_email))";// Set Primary Key

        db.execSQL(sql_create_invitation);


        // Delete the Database : This will wipe ... everything!
        // Useful to just keep here... for now
//                db.close();
//                _context.deleteDatabase(MOVIE_DB_NAME);
    }


    /**
     * Connect to Database. Lazy Connection (Only Connects when needed)
     * @return
     */
    static private synchronized SQLiteDatabase connect()
    {
        // Check if Database Is Open
        if (_database == null || !_database.isOpen())
        {
            // Open SQL DB
            _database = _context.openOrCreateDatabase(MOVIE_DB_NAME,
                    SQLiteDatabase.CREATE_IF_NECESSARY, null);

            Log.d("movies", "SQLLite Database Opened");
        }

        return _database;
    }


    /**
     * Close the Database
     * @return
     */
    static public void close()
    {
        // Check if Database is Open
        if (_database != null && _database.isOpen())
        {
            Log.d("movies", "SQLLite Database Closed");

            _database.close();
            _database = null;
        }
    }


    /**
     * Get Movie from the SQL Database
     * @param id
     */
    static public Movie getMovie(String id)
    {
        Log.v("movies", "SQL: Search = '" + id + "'");

        // Get Instance of Database
        SQLiteDatabase db = connect();

        // Query Database
        Cursor dbQuery = db.query("movies", null, "movie_id = ?", new String[]{id},
                null, null, null, "1");

        // Validate
        if (dbQuery == null)
        {
            Log.d("movies", "SQL: Movie '" + id + "': Invalid Result");
            db.close();
            return null;
        }

        // Get Movie information form the results
        Movie movie = null;
        while (dbQuery.moveToNext())
        {
            if (! dbQuery.isFirst())
                break;

            // Get Parameters
            String movie_id = dbQuery.getString(dbQuery.getColumnIndex("movie_id"));
            String movie_title = dbQuery.getString(dbQuery.getColumnIndex("movie_title"));
            String movie_year = dbQuery.getString(dbQuery.getColumnIndex("movie_year"));
            String movie_summary = dbQuery.getString(dbQuery.getColumnIndex("movie_summary"));
            String movie_plot = dbQuery.getString(dbQuery.getColumnIndex("movie_plot"));
            String movie_poster = dbQuery.getString(dbQuery.getColumnIndex("movie_poster"));

            float movie_rating = dbQuery.getFloat(dbQuery.getColumnIndex("movie_rating"));

            // Create Movie
            movie = new Movie(movie_id, movie_title, movie_year, movie_summary,
                    movie_plot, movie_poster, movie_rating);
        }

        if (movie != null)
            Log.d("movies", "SQL: Movie '" + id + "': Found in SQL Database");
        else
            Log.d("movies", "SQL: Movie '" + id + "': Not found in SQL Database");

        return movie;
    }


    /**
     * Stores the Movie in the SQL Database
     */
    static public void storeMovie(Movie movie)
    {
        if (movie == null)
            return ;

        SQLiteDatabase db;

        try
        {
            String query_movie_exists = "SELECT COUNT(*) FROM movies WHERE movie_id=?";
            String query_movie_insert = "INSERT INTO movies VALUES(?, ?, ?, ?, ? ,?, ?)";

            // Get Instance to Database
            db = connect();

            // Build Existance Statement
            SQLiteStatement sql_movie_exists = db.compileStatement(query_movie_exists);
            sql_movie_exists.bindString(1, movie.getId());

            // Check for Existence
            long count = sql_movie_exists.simpleQueryForLong();
            if (count == 1)
            {
                Log.i("movies", "SQL: Movie '" + movie.getId() + "': Exists");
                return;
            }

            // Build Insertion Statement
            SQLiteStatement sql_movie_insert = db.compileStatement(query_movie_insert);
            sql_movie_insert.bindString(1, movie.getId());
            sql_movie_insert.bindString(2, movie.getTitle());
            sql_movie_insert.bindString(3, movie.getYear());
            sql_movie_insert.bindString(4, movie.getSummary());
            sql_movie_insert.bindString(5, movie.getPlot());
            sql_movie_insert.bindString(6, movie.getPosterUrl());
            sql_movie_insert.bindDouble(7, 0.0);

            // Insert
            long result = sql_movie_insert.executeInsert();

            if (result == -1)
                Log.i("movies", "SQL: Movie '" + movie.getId() + "': Failed Inserting");
            else
                Log.i("movies", "SQL: Movie '" + movie.getId() + "': Inserted");
        }

        catch (Exception e)
        {
            Log.d("movies", "SQL: Exception Occurred while inserting movie");
            Log.e("movies", e.getMessage());
            e.printStackTrace();
        }

        finally
        {

        }
    }


    /**
     * Search for movies
     * @param search
     * @return
     */
    static public ArrayList<String> searchMovies(String search)
    {
        ArrayList<String> movies = new ArrayList<>();

        SQLiteDatabase db = connect();

        Cursor query = db.query(MOVIE_TABLE_NAME, new String[]{"movie_id"},
                "LOWER(movie_title) LIKE '%" + search.toLowerCase() + "%'",
                null, null, null, null);

        if (query == null)
        {
            return null;
        }

        while (query.moveToNext())
        {
            String movie_id = query.getString(query.getColumnIndex("movie_id"));
            movies.add(movie_id);
        }

        return movies;
    }


    /**
     *
     * @param id
     * @param rating
     */
    static public synchronized boolean updateMovieRating(final String id, final float rating)
    {
        SQLiteDatabase db;

        try
        {
            // Get Instance to Database
            db = connect();

            // Query to Execute
            String query_movie_update = "UPDATE movies SET movie_rating=? WHERE movie_id=?";

            // Compile Statement
            SQLiteStatement sql_movie_update = db.compileStatement(query_movie_update);

            // Bind Parameters
            sql_movie_update.bindDouble(1, rating);
            sql_movie_update.bindString(2, id);

            // Execute Statement
            long result = sql_movie_update.executeUpdateDelete();
            if (result == -1)
                Log.i("movies", "SQL: Movie '" + id + "': Rating failed to update");
            else
                Log.i("movies", "SQL: Movie '" + id + "': Rating Updated");

            return (result != -1);
        }

        catch (Exception e)
        {
            Log.d("movies", "SQL: Exception Occurred while inserting movie");
            Log.e("movies", e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


    /**
     *
     */
    static public ArrayList<Party> loadParties()
    {
        ArrayList<Party> parties = new ArrayList<Party>();

        // Obtain instance of Database Connection
        SQLiteDatabase db = connect();

        Log.d("movies", "SQL: Load Parties");

        // Select All Parties!
        // Query Database
        Cursor queryParty = db.query("parties", null, null, null, null, null, null, null);


        // Validate
        if (queryParty == null)
        {
            Log.d("movies", "SQL: No Parties");
            return null;
        }

        // Loop through parties
        while (queryParty.moveToNext())
        {
            String party_id = queryParty.getString(queryParty.getColumnIndex("party_id"));
            String party_movie_id = queryParty.getString(queryParty.getColumnIndex("party_movie_id"));

            String party_venue = queryParty.getString((queryParty.getColumnIndex("party_venue")));
            String party_location = queryParty.getString(queryParty.getColumnIndex("party_geolocation"));

            String party_datetime = queryParty.getString(queryParty.getColumnIndex("party_date"));

            Party party = new Party(party_id, party_movie_id);
            party.setVenue(party_venue);
            party.setLocation(party_location);

            party.setDate(ConvertUtil.convertStringToDateTime(party_datetime, "yyyy-MM-dd HH:mm:ss"));

            // Add to list
            parties.add(party);

            Log.d("movies", "Create Party: " + party_id + ", " + party_movie_id + ", " + party_datetime);
        }

        // Load Invites
        loadInvites(parties);

        return parties;
    }


    /**
     *
     * @param parties
     */
    static private void loadInvites(Collection<Party> parties)
    {
        // Obtain instance of Database Connection
        SQLiteDatabase db = connect();

        // Setup Invitation List
        Cursor queryInvites = db.query(INVITATION_TABLE_NAME, null, null, null, null, null, null, null);

        // Validate
        if (queryInvites == null)
        {
            Log.d("movies", "SQL: No Invitations");
            return;
        }

        // Loop through invitations
        while (queryInvites.moveToNext())
        {
            String party_id = queryInvites.getString(queryInvites.getColumnIndex("party_id"));
            String email = queryInvites.getString(queryInvites.getColumnIndex("invite_email"));

            // Get Party to add invitation too
            Party party = null;
            for (Party p : parties)
            {
                // Matches ID
                if (p.getId().equals(party_id))
                {
                    party = p;
                    break;
                }
            }

            // Add Email to Invite List
            if (party != null)
                party.getInvited().add(email);
        }
    }


    /**
     *
     * @param party
     */
    static public boolean createParty(Party party)
    {
        Log.d("movies", "SQL: Creating Party");

        // Obtain instance of Database Connection
        SQLiteDatabase db = connect();

        // Create Party in DB
        String query_party_create = "INSERT INTO parties VALUES(?, ?, ?, ?, ?)";

        // Create Statement
        SQLiteStatement sql_party_create = db.compileStatement(query_party_create);

        // Bind Parameters
        //id, movie_id, date, venue, geoloc
        sql_party_create.bindString(1, party.getId());
        sql_party_create.bindString(2, party.getMovieId());

        sql_party_create.bindString(3,
                ConvertUtil.convertDataTimeToDatebase(party.getDateTime()));

        sql_party_create.bindString(4, party.getVenue());
        sql_party_create.bindString(5, party.getLocation());

        long result = sql_party_create.executeInsert();

        if (result == -1)
            Log.i("movies", "SQL: Party '" + party.getId() + "': Failed Creating");
        else
            Log.i("movies", "SQL: Party '" + party.getId() + "': Created");

        return (result != -1);
    }


    /**
     *
     * @param party
     */
    static public boolean updateParty(Party party)
    {
        // Obtain instance of Database Connection
        SQLiteDatabase db = connect();

        // SQL
        String query_party_update = "UPDATE parties SET ";
        query_party_update += "party_venue=?,";
        query_party_update += "party_geolocation=?,";
        query_party_update += "party_date=?";
        query_party_update += " WHERE party_id=?";

        // Compile Statement
        SQLiteStatement sql_party_update = db.compileStatement(query_party_update);
        sql_party_update.bindString(1, party.getVenue());
        sql_party_update.bindString(2, party.getLocation());
        sql_party_update.bindString(3, ConvertUtil.convertDataTimeToDatebase(party.getDateTime()));
        sql_party_update.bindString(4, party.getId());

        long result = sql_party_update.executeUpdateDelete();

        if (result == -1)
            Log.i("movies", "SQL: Party '" + party.getId() + "': Failed Updating");
        else
            Log.i("movies", "SQL: Party '" + party.getId() + "': Updated");

        // Update Invitation List
        // Basic Method
        // Clear all invites where party=party.id();
        // Add new invites.
        deleteInvites(party.getId());

        String query_invite_insert = "INSERT INTO invitations VALUES(?, ?)";

        SQLiteStatement sql_invite_insert = db.compileStatement(query_invite_insert);

        for (String invited : party.getInvited())
        {
            sql_invite_insert.bindString(1, party.getId());
            sql_invite_insert.bindString(2, invited);

            long insert_result = sql_invite_insert.executeInsert();

            if (insert_result == -1)
                Log.i("movies", "SQL: Party '" + party.getId() + "': Failed Inserting Invite");
        }

        Log.i("movies", "SQL: Party Invites '" + party.getId() + "': Created");

        return (result != -1);
    }


    /**
     * Delete a party
     */
    static public boolean deleteParty(String partyId)
    {
        // Delete Invites
        deleteInvites(partyId);

        // Obtain instance of Database Connection
        SQLiteDatabase db = connect();

        // Create Party in DB
        String query_party_create = "DELETE FROM parties WHERE party_id=?";

        // Create Statement
        SQLiteStatement sql_party_create = db.compileStatement(query_party_create);

        sql_party_create.bindString(1, partyId);


        long result = sql_party_create.executeUpdateDelete();

        if (result == -1)
            Log.i("movies", "SQL: Party '" + partyId + "': Failed Deleting");
        else
            Log.i("movies", "SQL: Party '" + partyId + "': Deleted");

        return (result != -1);
    }

    /**
     * Delete Invites
     */
    static private boolean deleteInvites(String partyId)
    {
        // Obtain instance of Database Connection
        SQLiteDatabase db = connect();

        // Create Party in DB
        String query_invite_delete = "DELETE FROM invitations WHERE party_id=?";

        // Create Statement
        SQLiteStatement sql_invite_delete = db.compileStatement(query_invite_delete);

        sql_invite_delete.bindString(1, partyId);


        long result = sql_invite_delete.executeUpdateDelete();

        if (result == -1)
            Log.i("movies", "SQL: Party Invites '" + partyId + "': Failed Deleting");
        else
            Log.i("movies", "SQL: Party Invites '" + partyId + "': Deleted");

        return (result != -1);
    }
}
