package me.everything.jerry.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.text.Selection;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import me.everything.jerry.db.Agenda;
import me.everything.jerry.db.AgendaDbHelper;


/**
 * Created by markkoltnuk on 3/27/15.
 */
public class ContactsUtils {

    private static final String TAG = ContactsUtils.class.getSimpleName();
    public static final String CONTACT_KEY = "contact";

    /**
     * @return
     */
    public static List<Contact> readContacts(Context context) {
        context = context.getApplicationContext();
        Cursor contactsCursor = null;
        try {
            contactsCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            int count = contactsCursor.getCount();
            if (count == 0) {
                Log.w(TAG, "readContacts() no contacts found!");
                return Collections.emptyList();
            }

            AgendaDbHelper dbHelper = AgendaDbHelper.getInstance(context);

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

                // OMG what a hack!!
                Agenda agenda = dbHelper.getAgenda(phoneNumber);
                contacts.add(new Contact(id, name, phoneNumber, agenda));
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

    public static Contact getContactFromNumber(Context context, String number) {
        Cursor cursor = null;
        if (StringUtils.isNullOrEmpty(number)) {
            return null;
        }
        number = PhoneNumberUtils.toE164(context, number);
        if (StringUtils.isNullOrEmpty(number)) {
            return null;
        }
        try {
            cursor = context.getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                Log.w(TAG, "readContacts() no contacts found!");
                return null;
            }

            do {
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumber = PhoneNumberUtils.toE164(context, phoneNumber);
                if (!StringUtils.isNullOrEmpty(phoneNumber) && phoneNumber.equals(number)) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    // TODO - this null will cause problems at some point
                    return new Contact(id, name, phoneNumber, null);
                }
            } while (cursor.moveToNext());
        } catch (Exception e) {
            Log.e(TAG, "Failed reading contacts list", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static class Contact implements Parcelable {
        private String id;
        private String name;
        private String phoneNumber;
        private Agenda agenda;

        public Contact(String id, String name, String phoneNumber, Agenda agenda) {
            this.id = id;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.agenda = agenda;
        }

        public Contact(Parcel in) {
            String[] data = new String[3];
            in.readStringArray(data);
            id = data[0];
            name = data[1];
            phoneNumber = data[2];
            agenda = in.readParcelable(Agenda.class.getClassLoader());
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public Contact createFromParcel(Parcel in) {
                return new Contact(in);
            }

            public Contact[] newArray(int size) {
                return new Contact[size];
            }
        };

        public Agenda getAgenda() {
            return agenda;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(new String[]{id, name, phoneNumber});
            dest.writeParcelable(agenda, flags);
        }

        private static final int NO_COLOR = 0;
        private int color = NO_COLOR;

        public int getColor(Random random) {
            if (color != NO_COLOR)
                return color;
            color = random.nextInt(0xFFFFFF) + 0xFF000000;
            return color;
        }
    }
}
