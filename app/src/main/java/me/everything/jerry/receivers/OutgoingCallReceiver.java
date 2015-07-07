package me.everything.jerry.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.everything.jerry.db.AgendaDbHelper;
import me.everything.jerry.utils.ContactsUtils;

/**
 * Created by nitsan on 7/7/15.
 */
public class OutgoingCallReceiver extends BroadcastReceiver {
    private static final String TAG = OutgoingCallReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");
        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        Log.d(TAG, "number " + number);
        if (number == null) {
            return;
        }
        ContactsUtils.Contact contact = ContactsUtils.getContactFromNumber(context, number);
        if (contact == null) {
            return;
        }
        Log.d(TAG, "contact " + contact.getName());
        String agenda = AgendaDbHelper.getInstance(context).getAgenda(contact.getName());
        if (agenda == null) {
            return;
        }
        Log.d(TAG, "agenda " + agenda);
        JerryViewHolder.getInstance().addNonClickableView(context, number, contact, agenda);
    }
}
