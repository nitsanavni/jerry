package me.everything.jerry.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.everything.jerry.db.Agenda;
import me.everything.jerry.db.AgendaDbHelper;
import me.everything.jerry.utils.PhoneNumberUtils;

/**
 * Created by nitsan on 7/7/15.
 */
public class OutgoingCallReceiver extends BroadcastReceiver {
    private static final String TAG = OutgoingCallReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");
        String number = getResultData();
        if (number == null) {
            // No reformatted number, use the original
            number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }
        Log.d(TAG, "number " + number);
        if (number == null) {
            return;
        }
        number = PhoneNumberUtils.toE164(context, number);
        Agenda agenda = AgendaDbHelper.getInstance(context).getAgenda(number);
        if (agenda == null) {
            return;
        }
        Log.d(TAG, "agenda " + agenda.getAgenda());
        JerryViewHolder.getInstance().addNonClickableView(context, agenda);
    }
}
