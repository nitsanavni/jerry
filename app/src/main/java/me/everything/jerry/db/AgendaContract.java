package me.everything.jerry.db;

import android.provider.BaseColumns;

/**
 * Created by nitsan on 7/7/15.
 */
public class AgendaContract {
    public AgendaContract() {
    }

    public static abstract class AgendaEntry implements BaseColumns {
        public static final String TABLE_NAME = "agendaItem";
        public static final String COLUMN_NAME_AGENDA = "agenda";
        public static final String COLUMN_NAME_CONTACT_NAME = "contactName";
        public static final String COLUMN_NAME_IS_CHECKED = "isChecked";
    }
}
