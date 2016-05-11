package com.spirit.movies.model.cache;

import com.spirit.movies.model.Party;
import com.spirit.movies.model.sql.IFeedbackHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by spirit on 4/10/2015.
 */
public class CachePartyHelper
{
    private HashMap<String, Party> _parties;

    /**
     *
     */
    public CachePartyHelper()
    {
        _parties = new HashMap<String, Party>();
    }


    /**
     * Creates a Party
     * @param movieId
     */
    public Party createParty(String movieId)
    {
        // Generate a UUID
        UUID uuid = UUID.randomUUID();

        // Create Party
        Party party = new Party(uuid.toString(), movieId);

        // Put in HashMap
        _parties.put(uuid.toString(), party);

        // Put in SQL Database

        // Put in Firebase

        return party;
    }

    /**
     * Update the Party
     * @param partyId
     * @return
     */
    public Party updateParty(String partyId, IFeedbackHandler feedback)
    {
        // Get Party
        Party party = getParty(partyId);

        return party;
    }


    /**
     * Removes the party from the map
     * @param partyId
     */
    public void deleteParty(String partyId)
    {
        _parties.remove(partyId);
    }

    /**
     * Returns a party with the matching party ID
     * @param id
     * @return
     */
    public Party getParty(String id)
    {
        return _parties.get(id);
    }


    /**
     *
     * @param party
     */
    public void putParty(Party party)
    {
        if (party == null)
            return;

        _parties.put(party.getId(), party);
    }


    /**
     * Searches for all parties for that movie
     * @param movieId
     * @return
     */
    public Collection<Party> getPartyByMovieId(String movieId)
    {
        Collection<Party> partyList = _parties.values();

        ArrayList<Party> parties = new ArrayList<>();

        // Find all the parties for the movie
        for (Party party : partyList)
        {
            // For this movie ?
            if (party.getMovieId().equals(movieId))
            {
                parties.add(party);
            }
        }

        return Collections.unmodifiableList(parties);
    }
}
