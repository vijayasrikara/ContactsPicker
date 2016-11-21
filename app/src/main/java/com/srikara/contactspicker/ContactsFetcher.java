package com.srikara.contactspicker;

import android.app.Activity;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.LoaderManager;
import android.provider.ContactsContract;
import android.content.Loader;

import com.srikara.contactspicker.model.Contact;
import com.srikara.contactspicker.model.ContactEmail;
import com.srikara.contactspicker.model.ContactPhone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijayasrikaradas on 21/11/16.
 */

public class ContactsFetcher {

    private final Activity mContext;

    private static final int CONTACTS_QUERY_ID = 1;

    private static String DISPLAY_NAME_COMPAT = Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
            ContactsContract.Contacts.DISPLAY_NAME;

    String[] contactsProjection = new String[]{
            ContactsContract.Contacts._ID,
            DISPLAY_NAME_COMPAT,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.LOOKUP_KEY
    };

    String contactsSelection = "((" + DISPLAY_NAME_COMPAT + " NOTNULL) AND ("
            + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
            + DISPLAY_NAME_COMPAT + " != '' ))";

    String contactsSort = ContactsContract.Contacts.DISPLAY_NAME
            + " COLLATE LOCALIZED ASC";

    private ContactLoaderCallback callback;

    public ContactsFetcher(Activity context){
        mContext = context;
    }

    public void fetchAll(ContactLoaderCallback callback){
        this.callback = callback;
        mContext.getLoaderManager().initLoader(CONTACTS_QUERY_ID, null, loaderCallbacks);
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return  new CursorLoader(mContext,
                    ContactsContract.Contacts.CONTENT_URI,
                    contactsProjection,
                    contactsSelection,
                    null,
                    contactsSort);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            StoreContactTask storeContactTask = new StoreContactTask();
            storeContactTask.execute(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private class StoreContactTask extends AsyncTask<Cursor, Void, List<Contact>>{

        @Override
        protected List<Contact> doInBackground(Cursor... params) {

            List<Contact> contactList = fetchContactInformation(params[0]);

            return contactList;
        }

        @Override
        protected void onPostExecute(List<Contact> contactList) {

            callback.onLoadCompleted(contactList);
        }
    }

    private List<Contact> fetchContactInformation(Cursor cursor) {

        List<Contact> listContacts = new ArrayList<>();

        if (cursor.moveToFirst()) {

            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            do {
                String contactId = cursor.getString(idIndex);
                String contactDisplayName = cursor.getString(nameIndex);
                Contact contact = new Contact();

                contact.setId(contactId);
                contact.setName(contactDisplayName);
                contact.setNumberList(fetchContactNumbers(contactId));
                contact.setEmailList(fetchContactEmails(contactId));

                listContacts.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listContacts;
    }

    public List<ContactPhone> fetchContactNumbers(String contactId) {

        List<ContactPhone> phoneList = new ArrayList<>();

        final String[] numberProjection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        };

        Cursor phone = mContext.getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                numberProjection,
                ContactsContract.Data.CONTACT_ID + "=?",
                new String[]{contactId},
                null);

        if (phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            final int contactTypeColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            final int contactIdColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);

            while (!phone.isAfterLast()) {
                final String number = phone.getString(contactNumberColumnIndex);
                final String id = phone.getString(contactIdColumnIndex);
                final int type = phone.getInt(contactTypeColumnIndex);

                CharSequence phoneType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(mContext.getResources(), type, "");
                ContactPhone contactPhone = new ContactPhone(number, phoneType.toString());

                phoneList.add(contactPhone);

                phone.moveToNext();
            }
        }

        phone.close();

        return phoneList;
    }

    public List<ContactEmail> fetchContactEmails(String contactId) {

        List<ContactEmail> emailList = new ArrayList<>();

        final String[] emailProjection = new String[]{
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Email.TYPE,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
        };

        Cursor email = mContext.getContentResolver()
                .query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                emailProjection,
                ContactsContract.Data.CONTACT_ID + "=?",
                new String[]{contactId},
                null);

        if (email.moveToFirst()) {
            final int contactEmailColumnIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            final int contactTypeColumnIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE);
            final int contactIdColumnsIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);

            while (!email.isAfterLast()) {
                final String address = email.getString(contactEmailColumnIndex);
                final String id = email.getString(contactIdColumnsIndex);
                final int type = email.getInt(contactTypeColumnIndex);
                String customLabel = "Custom";
                CharSequence emailType = ContactsContract.CommonDataKinds.Email.getTypeLabel(mContext.getResources(), type, customLabel);
                ContactEmail contactEmail = new ContactEmail(address, emailType.toString());

                emailList.add(contactEmail);

                email.moveToNext();
            }
        }

        email.close();

        return emailList;
    }
}
