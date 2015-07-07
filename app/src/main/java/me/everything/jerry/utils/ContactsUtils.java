package me.everything.jerry.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by markkoltnuk on 3/27/15.
 */
public class ContactsUtils {

    private static final String TAG = ContactsUtils.class.getSimpleName();

    /**
     * @return
     */
    public static List<Contact> readContacts(Context context) {
        context = context.getApplicationContext();
        Cursor contactsCursor = null;
        try {
            contactsCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            int count = contactsCursor.getCount();
            if (count == 0) {
                Log.w(TAG, "readContacts() no contacts found!");
                return Collections.emptyList();
            }

            Log.w(TAG, "readContacts() found " + count + " contacts, syncing...");
            List<Contact> contacts = new ArrayList<>(count);
            contactsCursor.moveToFirst();
            while (!contactsCursor.isAfterLast()) {
                String id = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String name = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                // standardize phone numbers
                phoneNumber = PhoneNumberUtils.toE164(context, phoneNumber);

                if (StringUtils.isNullOrEmpty(name) || StringUtils.isNullOrEmpty(phoneNumber)) {
                    contactsCursor.moveToNext();
                    continue;
                }

                contacts.add(new Contact(id, name, phoneNumber));
                contactsCursor.moveToNext();
            }
            return contacts;
        } catch (Exception e) {
            Log.e(TAG, "Failed reading contacts list", e);
        } finally {
            if (contactsCursor != null) {
                contactsCursor.close();
            }
        }
        return Collections.emptyList();
    }

    public static Uri getPhotoUriFromID(Context context, String id) {
        context = context.getApplicationContext();
        Cursor cur = null;
        try {
            cur = context.getContentResolver()
                    .query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID
                                    + "="
                                    + id
                                    + " AND "
                                    + ContactsContract.Data.MIMETYPE
                                    + "='"
                                    + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                                    + "'", null, null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        Uri person = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
        return Uri.withAppendedPath(person,
                ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public static Bitmap retrieveContactPhoto(Context context, String contactId) {
        context = context.getApplicationContext();
        InputStream inputStream = null;
        try {
            inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));
            if (inputStream != null) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return null;
    }

    public static class Contact {
        private String id;
        private String name;
        private String phoneNumber;

        public Contact(String id, String name, String phoneNumber) {
            this.id = id;
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

    }
}
