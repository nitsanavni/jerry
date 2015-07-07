package me.everything.jerry.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import me.everything.jerry.db.AgendaDbHelper;
import me.everything.jerry.utils.ContactsUtils;
import me.everything.jerry.utils.StringUtils;

/**
 * Created by nitsan on 7/7/15.
 */
public class PhoneStateReceiver extends BroadcastReceiver {

    private static final String TAG = PhoneStateReceiver.class.getSimpleName();

    public PhoneStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (StringUtils.isNullOrEmpty(state)) {
            return;
        }

        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Log.d(TAG, "idle");
            JerryViewHolder.getInstance().removeView(context);
            return;
        }

        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            Log.d(TAG, "ringing");
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d(TAG, "number " + number);
            ContactsUtils.Contact contact = ContactsUtils.getContactFromNumber(context, number);
            if (contact == null) {
                return;
            }
            String agenda = AgendaDbHelper.getInstance(context).getAgenda(contact.getName());
            if (agenda == null) {
                // no agenda for this caller
                return;
            }
            JerryViewHolder.getInstance().addNonClickableView(context, number, contact, agenda);
            return;
        }

        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            Log.d(TAG, "off hook");
            return;
        }

    }


}
