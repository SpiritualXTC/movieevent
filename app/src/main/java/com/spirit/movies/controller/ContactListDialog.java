package com.spirit.movies.controller;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.spirit.movies.R;

import java.util.ArrayList;


/**
 * Created by s3435406 on 18/08/2015.
 */
public class ContactListDialog extends Dialog
{
    /**
     * Stores a Contact
     */
    public class Contact
    {
        public String id;
        public String name;
        public String email;
        public String phone;

        public boolean invited = false;
        public String toString()
        {
            return name;
        }
    }

    /**
     * Event Handler for creating party
     */
    public interface OnCreatePartyListener
    {
        void onCreate(ArrayList<String> invited);
    }


    private OnCreatePartyListener onCreateParty = null;



    // Currently Invited List : Auto checks the items
    private ArrayList<String> _contactCurrent = null;

    private ArrayList<Contact> _contacts = null;

    /**
     *
     * @param c
     * @param contacts
     */
    public ContactListDialog(Context c, ArrayList<String> contacts)
    {
        super(c);


        _contactCurrent = contacts;

        Log.d("movies", "Invited = " + _contactCurrent.size());
        for (String s : _contactCurrent)
        {
            Log.d("movies", "INvited = " + s);
        }

        // Create Contacts Map
        _contacts = new ArrayList<>();
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_party_dialog);
        setTitle(getContext().getResources().getString(R.string.movie_invite_contacts));


        // Get Contact Data
        Cursor contactData = getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,null,null, null);
        while (contactData.moveToNext())
        {
            // Create Contact
            Contact contact = new Contact();

            // Get Contact ID
            String id = contactData.getString(contactData.getColumnIndex(ContactsContract.Contacts._ID));
            contact.id = id;

            // Get Contact Name
            String name = contactData.getString(contactData.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contact.name = name;


            // Pull Phone Numbers for matching ID....
            Cursor phonenos = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

            // Loop through phone numbers, just grab the first
            while (phonenos.moveToNext())
            {
                String phoneno = phonenos.getString(phonenos.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contact.phone = phoneno;

                break;
            }
            phonenos.close();

            // Pull Email(s) for contact matching ID....
            Cursor emails = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);

            // Loop through emails, just grab the first
            while (emails.moveToNext())
            {
                String email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                contact.email = email;
                break;
            }
            emails.close();

            Log.d("movies", "Contact: ID = " + contact.id + ", Name = " + contact.name + ", Number = " + contact.phone + ", E-Mail = " + contact.email);

            // Add Contact to Map, if their is an email.
            // Uses the Email as a key, for simplicity
            // Assignment only required storing the email,
            // so it can be used as the lookup :)
            if (contact.email != null)
            {
                // Current Invited List : Set Invited Flag
                if (_contactCurrent != null)
                {
                    for (String s : _contactCurrent)
                    {
                        if (s.equals(contact.email))
                        {
                            contact.invited = true;
                            break;
                        }
                    }
                }

                _contacts.add(contact);
            }
        }
        contactData.close();

        // Create Adapter
        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(getContext(), android.R.layout.simple_list_item_checked, _contacts);

        // Set Up List
        ListView contacts = (ListView)findViewById(R.id.contact_list_view);
        contacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        contacts.setAdapter(adapter);

        // Check items for people already in attendance :)
        for (int i=0; i<_contacts.size(); ++i)
        {
            if (_contacts.get(i).invited == true)
            {
                contacts.setItemChecked(i, true);
            }
        }

        // Setup event handlers for create button
        Button create = (Button)findViewById(R.id.party_create);
        create.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createParty();

                dismiss();
            }
        });

        // Setup event handlers for cancelling
        Button cancel = (Button)findViewById(R.id.party_cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
    }



    /**
     * Set event listener for confirmation of creating party.
     * @param listener
     */
    public void setCreatePartyListener(OnCreatePartyListener listener)
    {
        onCreateParty = listener;
    }


    /**
     * Create the Party
     */
    private void createParty()
    {
        ListView contactList = (ListView)findViewById(R.id.contact_list_view);

        ArrayList<String> invited = new ArrayList<String>();

        SparseBooleanArray checked = contactList.getCheckedItemPositions();

        for (int i=0; i<checked.size(); ++i)
        {

            if (checked.valueAt(i))
            {
                Contact contact = _contacts.get(checked.keyAt(i));

                Log.d("movies", checked.keyAt(i) + "");
             //   Log.d("movies", contacts.get(checked.keyAt(i)));
              //  invited.add(contacts.get);

                Log.d("movies", "Contact: ID = " + contact.id + ", Name = " + contact.name + ", Number = " + contact.phone + ", E-Mail = " + contact.email);

                // Add to Invited List
                invited.add(contact.email);
            }

        }

        if (onCreateParty != null)
        {
            onCreateParty.onCreate(invited);
        }
    }
}
