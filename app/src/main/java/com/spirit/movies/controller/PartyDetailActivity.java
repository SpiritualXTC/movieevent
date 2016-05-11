package com.spirit.movies.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.spirit.movies.R;
import com.spirit.movies.model.MovieFacade;
import com.spirit.movies.model.Party;
import com.spirit.movies.model.sql.IFeedbackHandler;
import com.spirit.movies.util.ConvertUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 *
 */
public class PartyDetailActivity extends ActionBarActivity
{
    static public final String ACTION_CREATE = "create";
    static public final String ACTION_EDIT = "edit";


    private ArrayAdapter<String> _invitedAdapter = null;
    private ArrayList<String> _invitedList = null;
    private Calendar _calendar;

    private boolean _create = false;


    private Party _party;

    /**
     *
     */
    public PartyDetailActivity()
    {
        _invitedList = new ArrayList<>();
        _calendar = Calendar.getInstance();
    }


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party_detail);

        // This activity can be launched, as an edit or a create...
        String action = getIntent().getExtras().get("action").toString();

        Log.d("movies", "Party Action = " + action);


        if (action.equals(ACTION_CREATE))
        {
            _create = true;

            // Get the Movie ID from the parameters passed to the intent
            String movie_id = getIntent().getExtras().get("movie").toString();

            // Create the Party for this Movie
            _party = MovieFacade.getInstance().createParty(movie_id, null);
        }
        else if (action.equals(ACTION_EDIT))
        {
            _create = false;

            // Get Pointer to the party
            String party_id = getIntent().getExtras().get("party").toString();

            // Get the Party
            _party = MovieFacade.getInstance().getParty(party_id);
        }

        // Update Calendar
        copyCalendarFields(_calendar, _party.getCalendar());

        // Copy Invited List
        for (String s : _party.getInvited())
            _invitedList.add(s);



        // Set Fields
        TextView date = (TextView)findViewById(R.id.party_display_date);
        TextView time = (TextView)findViewById(R.id.party_display_time);
        TextView venue = (TextView)findViewById(R.id.party_display_venue);
        TextView location = (TextView)findViewById(R.id.party_display_geolocation);
        TextView invited = (TextView) findViewById(R.id.party_display_invited);
        ListView invitations = (ListView)findViewById(R.id.party_display_invitations);

        date.setText(ConvertUtil.convertDateString(this, _party.getDateTime()));
        time.setText(ConvertUtil.convertTimeString(this, _party.getDateTime()));
        venue.setText(_party.getVenue());
        location.setText(_party.getLocation());
        invited.setText("" + _invitedList.size());



        // Invitation List
        _invitedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _invitedList);
        invitations.setAdapter(_invitedAdapter);


        // Set Event Handlers
        Button button_invite = (Button)findViewById(R.id.movie_display_invite_contacts);
        Button button_set_date = (Button) findViewById(R.id.movie_display_date_set);
        Button button_set_time = (Button) findViewById(R.id.movie_display_time_set);

        // Set Invited List
        button_invite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inviteContacts();
            }
        });

        // Set Date Handler
        button_set_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDateDialog();
            }
        });

        // Set Time Handler
        button_set_time.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showTimeDialog();
            }
        });
    }


    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_party_detail, menu);

        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
        // Update Party
        case R.id.party_action_update:
            // Delete Party
            updateParty();
            break;

        // Revert Changes
        case R.id.party_action_revert:
            // Finish with this activity
            finish();
            break;

        // Delete Party
        case R.id.party_action_delete:
            // Delete Party
            deleteParty();
            break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Shows the Time Picker Dialog
     */
    private void showTimeDialog()
    {
        // Add Arguments to Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("hour", _calendar.get(Calendar.HOUR_OF_DAY));
        bundle.putInt("minute", _calendar.get(Calendar.MINUTE));

        // Create Dialog
        TimeDialog timeDialog = new TimeDialog();
        timeDialog.setArguments(bundle);

        // Set Event Handler
        timeDialog.setOnTimeListener(new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                _calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                _calendar.set(Calendar.MINUTE, minute);

                // Set Time
                TextView party_time = (TextView) findViewById(R.id.party_display_time);
                party_time.setText(ConvertUtil.convertTimeString(getApplicationContext(), _calendar.getTime()));
            }
        });

        // Show Dialog
        timeDialog.show(getFragmentManager(), "time");
    }

    /**
     * Shows the Date Picker Dialog
     */
    private void showDateDialog()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("year", _calendar.get(Calendar.YEAR));
        bundle.putInt("month", _calendar.get(Calendar.MONTH));
        bundle.putInt("day", _calendar.get(Calendar.DAY_OF_MONTH));

        Log.d("movies", "set date");
        DateDialog dateDialog = new DateDialog();

        dateDialog.setArguments(bundle);

        // Set Event Handler
        dateDialog.setOnDateListener(new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                _calendar.set(Calendar.YEAR, year);
                _calendar.set(Calendar.MONTH, month);
                _calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Set Date
                TextView party_date = (TextView) findViewById(R.id.party_display_date);
                party_date.setText(ConvertUtil.convertDateString(getApplicationContext(), _calendar.getTime()));
            }
        });

        // Show Dialog
        dateDialog.show(getFragmentManager(), "date");
    }


    /**
     * Updates the Party Details
     */
    private void updateParty()
    {
        // Party Exists?
        if (_party == null)
            return;

        TextView party_venue = (TextView)findViewById(R.id.party_display_venue);
        TextView party_geolocation = (TextView)findViewById(R.id.party_display_geolocation);

        // Update Venue & GeoLocation
        _party.setVenue(party_venue.getText().toString());
        _party.setLocation(party_geolocation.getText().toString());

        // Update Calendar
        copyCalendarFields(_party.getCalendar(), _calendar);

        // Copy Temporary Invited List to Party List
        _party.getInvited().clear();
        for (String s : _invitedList)
            _party.getInvited().add(s);

        // Update the Party
        MovieFacade.getInstance().updateParty(_party.getId(), new IFeedbackHandler()
        {
            @Override
            public void onComplete(boolean success)
            {
                finish();
                Toaster.displayToast(PartyDetailActivity.this
                        , R.string.feedback_party_update, Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * Deletes the Party
     */
    protected void deleteParty()
    {
        System.out.println("delete");

        MovieFacade.getInstance().deleteParty(_party.getId(), new IFeedbackHandler()
        {
            @Override
            public void onComplete(boolean success)
            {
                finish();

                Toaster.displayToast(PartyDetailActivity.this
                        , R.string.feedback_party_delete, Toast.LENGTH_SHORT);
            }
        });
    }




    /**
     *
     * @param toastText
     */
//    protected void displayToast(final CharSequence toastText)
//    {
//        PartyDetailActivity.this.runOnUiThread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                // Display a Toast for feedback to the use
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(getApplicationContext(), toastText, duration);
//                toast.show();
//            }
//        });
//    }



    /**
     * Invite Contacts
     */
    private void inviteContacts()
    {
        // Create Contacts Dialog
        ContactListDialog cld = new ContactListDialog(PartyDetailActivity.this, _invitedList);

        // Set a Listener
        cld.setCreatePartyListener(new ContactListDialog.OnCreatePartyListener()
        {
            @Override
            public void onCreate(ArrayList<String> invited)
            {
                // Rebuild Temporary List
                _invitedList.clear();
                for (String s : invited)
                {
                    Log.d("movies", "Inviting: " + s);
                    _invitedList.add(s);
                }

                // Reassign Temporary Invited List :)
                //_invitedList = invited;

                // Dataset changed
                _invitedAdapter.notifyDataSetChanged();

                // Set Invited List
                TextView party_invited = (TextView) findViewById(R.id.party_display_invited);
                party_invited.setText("" + _invitedList.size());


            }
        });

        // Show the Dialog
        cld.show();
    }




    /**
     * Copy fields from the source calender to the destination calendar
     * @param dest
     * @param src
     */
    public void copyCalendarFields(Calendar dest, Calendar src)
    {
        dest.set(Calendar.YEAR, src.get(Calendar.YEAR));
        dest.set(Calendar.MONTH, src.get(Calendar.MONTH));
        dest.set(Calendar.DAY_OF_MONTH, src.get(Calendar.DAY_OF_MONTH));
        dest.set(Calendar.HOUR_OF_DAY, src.get(Calendar.HOUR_OF_DAY));
        dest.set(Calendar.MINUTE, src.get(Calendar.MINUTE));
    }
}
