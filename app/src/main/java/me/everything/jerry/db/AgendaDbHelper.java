package me.everything.jerry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.everything.jerry.utils.ContactsUtils;

/**
 * Created by nitsan on 7/7/15.
 */
public class AgendaDbHelper extends SQLiteOpenHelper {

    private static final String TAG = AgendaDbHelper.class.getSimpleName();
    private static AgendaDbHelper sInstance;

    public static AgendaDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AgendaDbHelper(context);
        }
        return sInstance;
    }

    private static final String DATABASE_NAME = "agenda.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String INT_TYPE = " INTEGER";

    private static final java.lang.String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            AgendaContract.AgendaEntry.TABLE_NAME + " (" +
            AgendaContract.AgendaEntry._ID + " INTEGER PRIMARY KEY," +
            AgendaContract.AgendaEntry.COLUMN_NAME_AGENDA + TEXT_TYPE + COMMA_SEP +
            AgendaContract.AgendaEntry.COLUMN_NAME_IS_CHECKED + INT_TYPE + COMMA_SEP +
            AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NUMBER + TEXT_TYPE + COMMA_SEP +
            AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NAME + TEXT_TYPE + COMMA_SEP +
            AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NUMBER_SEEN + INT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AgendaContract.AgendaEntry.TABLE_NAME;

    private AgendaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void addAgendaItem(ContactsUtils.Contact contact, String text) {
        Log.d(TAG, "addAgendaItem, contact " + contact.getName() + ", text " + text + ", number " + contact.getPhoneNumber());
        String name = contact.getName();
        ContentValues values = new ContentValues();
        values.put(AgendaContract.AgendaEntry.COLUMN_NAME_AGENDA, text);
        values.put(AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NAME, name);
        if (exists(contact.getPhoneNumber())) {
            Log.d(TAG, "update");
            getWritableDatabase().updateWithOnConflict(
                    AgendaContract.AgendaEntry.TABLE_NAME,
                    values,
                    AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NUMBER + "=?",
                    new String[]{contact.getPhoneNumber()},
                    SQLiteDatabase.CONFLICT_IGNORE);
        } else {
            Log.d(TAG, "update");
            values.put(AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NUMBER, contact.getPhoneNumber());
            getWritableDatabase().insert(
                    AgendaContract.AgendaEntry.TABLE_NAME,
                    null,
                    values);
        }
        printAllDb();
    }

    // hack! should be done as sibgle pass on db
    private boolean exists(String number) {
        Log.d(TAG, "exists");
        Cursor cursor = null;
        try {
            String selection = AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NUMBER + "=?";
            String[] selectionArgs = new String[]{number};
            cursor = getReadableDatabase().query(
                    AgendaContract.AgendaEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);
            if (cursor == null || !cursor.moveToFirst()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public Agenda getAgenda(String number) {
        Log.d(TAG, "getAgenda");
        Cursor cursor = null;
        try {
            String selection = AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NUMBER + "=?";
            String[] selectionArgs = new String[]{number};
            cursor = getReadableDatabase().query(
                    AgendaContract.AgendaEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);
            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }

            String name = cursor.getString(cursor.getColumnIndex(AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NAME));
            String agendaText = cursor.getString(cursor.getColumnIndex(AgendaContract.AgendaEntry.COLUMN_NAME_AGENDA));
            Agenda agenda = new Agenda(name, number, agendaText);
            Log.d(TAG, "agenda: " + agenda.getAgenda() + ", name: " + agenda.getContactName());
            return agenda;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public void incrementSeen(String number) {
        Log.d(TAG, "incrementSeen");
        if (null == number) {
            return;
        }
        String id = AgendaContract.AgendaEntry._ID;
        String table = AgendaContract.AgendaEntry.TABLE_NAME;
        String seen = AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NUMBER_SEEN;
        String numberCol = AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NUMBER;
        String name = AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NAME;
        String agenda = AgendaContract.AgendaEntry.COLUMN_NAME_AGENDA;
        // the intention is to increment
        getWritableDatabase().execSQL(
                "INSERT OR REPLACE INTO " + table + " (" +
                        id + "," +
                        agenda + "," +
                        numberCol + "," +
                        seen + "," +
                        name + ") " +
                        "VALUES(" +
                        "(SELECT " + id + " FROM " + table + " WHERE " + numberCol + "='" + number + "')," +
                        "(SELECT " + agenda + " FROM " + table + " WHERE " + numberCol + "='" + number + "')," +
                        "'" + number + "'," +
                        "COALESCE((SELECT " + seen + " FROM " + table + " WHERE " + numberCol + "='" + number + "') + 1, 1 ), " +
                        "(SELECT " + name + " FROM " + table + " WHERE " + numberCol + "='" + number + "')" +
                        ");");
        printAllDb();
    }

    public void printAllDb() {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(
                    AgendaContract.AgendaEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            if (cursor == null || !cursor.moveToFirst()) {
                Log.d(TAG, "db empty");
                return;
            }

            do {
                String name = cursor.getString(cursor.getColumnIndex(AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NAME));
                String agenda = cursor.getString(cursor.getColumnIndex(AgendaContract.AgendaEntry.COLUMN_NAME_AGENDA));
                String number = cursor.getString(cursor.getColumnIndex(AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NUMBER));
                int seen = cursor.getInt(cursor.getColumnIndex(AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NUMBER_SEEN));
                Log.d(TAG, "printall: name: " + name + ", seen " + seen + ", agenda: " + agenda + ", number " + number);
            } while (cursor.moveToNext());

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
