package me.everything.jerry.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nitsan on 7/7/15.
 */
public class AgendaDbHelper extends SQLiteOpenHelper {

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
            AgendaContract.AgendaEntry.COLUMN_NAME_CONTACT_NAME + TEXT_TYPE +
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



}
