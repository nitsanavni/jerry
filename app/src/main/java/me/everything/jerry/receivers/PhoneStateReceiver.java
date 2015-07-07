package me.everything.jerry.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

import me.everything.jerry.R;
import me.everything.jerry.utils.StringUtils;

/**
 * Created by nitsan on 7/7/15.
 */
public class PhoneStateReceiver extends BroadcastReceiver {

    public PhoneStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (StringUtils.isNullOrEmpty(state)) {
            return;
        }

        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            JerryViewHolder.getInstance().removeView(context);
            return;
        }

        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) ||
                state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            JerryViewHolder.getInstance().addView(context, number);
        }

    }


}
