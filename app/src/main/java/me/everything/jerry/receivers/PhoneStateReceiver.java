package me.everything.jerry.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import me.everything.jerry.db.Agenda;
import me.everything.jerry.db.AgendaDbHelper;
import me.everything.jerry.services.JerryService;
import me.everything.jerry.utils.PhoneNumberUtils;
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
            JerryService.removeView(context);
            return;
        }

        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            Log.d(TAG, "incoming call ringing");
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d(TAG, "number " + number);
            if (number == null) {
                return;
            }
            number = PhoneNumberUtils.toE164(context, number);
            if (number == null) {
                return;
            }
            AgendaDbHelper dbHelper = AgendaDbHelper.getInstance(context);
            dbHelper.incrementSeen(number);
            Agenda agenda = dbHelper.getAgenda(number);
            if (agenda == null || (StringUtils.isNullOrEmpty(agenda.getAgenda()) && StringUtils.isNullOrEmpty(agenda.getAgendaSubject()))) {
                // no agenda for this caller
                return;
            }
            JerryService.addView(context, agenda);
            return;
        }

        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            Log.d(TAG, "off hook (either outgoing call or answered incoming call)");
            //JerryViewHolder.getInstance().offhook();
        }

    }


}
