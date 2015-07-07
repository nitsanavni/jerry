package me.everything.jerry.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by markkoltnuk on 3/24/15.
 */
public class DeviceUtils {

    public static String getDeviceId(Context context) {
        final String tmDevice, tmSerial, androidId;

        context = context.getApplicationContext();

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    public static String getUsername(Context context) {
        AccountManager manager = AccountManager.get(context.getApplicationContext());
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }

    public static String getCountryCode(Context context) {
        TelephonyManager tMgr = (TelephonyManager) context.getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tMgr.getNetworkCountryIso();
        if (!StringUtils.isNullOrEmpty(countryCode)) {
            return countryCode.toUpperCase();
        }
        return countryCode;
    }

    public static String getSelfPhoneNumber(Context context) {
        TelephonyManager tMgr = (TelephonyManager) context.getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String number = tMgr.getLine1Number();
        return PhoneNumberUtils.toE164(context, number);
    }

}
