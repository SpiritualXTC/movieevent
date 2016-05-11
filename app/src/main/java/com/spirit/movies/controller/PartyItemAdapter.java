package com.spirit.movies.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.spirit.movies.R;
import com.spirit.movies.model.Party;
import com.spirit.movies.util.ConvertUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by spirit on 5/10/2015.
 */
public class PartyItemAdapter extends ArrayAdapter<Party>
{
    /**
     *
     */
    public PartyItemAdapter(Context context, Collection<Party> parties)
    {
        super(context, R.layout.party_listitem, new ArrayList(parties));
    }


    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.party_listitem, parent, false);

        Party p = this.getItem(position);

        TextView venue = (TextView)view.findViewById(R.id.party_item_venue);
        venue.setText(p.getVenue());

        TextView invited = (TextView)view.findViewById((R.id.party_item_invited));
        invited.setText("" + p.getInvited().size());

        TextView date = (TextView)view.findViewById(R.id.party_item_date);
        date.setText(ConvertUtil.convertDateString(getContext(), p.getDateTime()));

        TextView time = (TextView)view.findViewById(R.id.party_item_time);
        time.setText(ConvertUtil.convertTimeString(getContext(), p.getDateTime()));

        return view;
    }
}
