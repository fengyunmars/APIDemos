package com.example.android.apis.provider.contacts;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.SimpleCursorAdapter;

import com.example.android.apis.R;

public class ContactsContractDemo extends ListActivity {

    public static final String CONTACTS_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI.toString();
    public static final String CONTACTS_CONTENT_LOOKUP_URI = ContactsContract.Contacts.CONTENT_LOOKUP_URI.toString();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts._ID,ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
        setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME}, new int[]{android.R.id.text1}));
//        setContentView(R.layout.activity_contacts_contract_demo);
    }
}
