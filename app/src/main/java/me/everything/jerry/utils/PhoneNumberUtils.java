package me.everything.jerry.utils;

import android.content.Context;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by nitsan on 6/11/15.
 */
public class PhoneNumberUtils {

    private static final String TAG = PhoneNumberUtils.class.getSimpleName();

    public static String toE164(Context context, String phoneNumber) {
        context = context.getApplicationContext();
        String ret = StringUtils.EMPTY_STRING;
        try {
            PhoneNumberUtil util = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number = util.parse(phoneNumber,
                    DeviceUtils.getCountryCode(context));
            // only care about mobile numbers
            if (util.isValidNumber(number) &&
                    util.getNumberType(number) == PhoneNumberUtil.PhoneNumberType.MOBILE) {
                // format to E164 - our chosen standard
                ret = util.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
                // remove the '+' from the start of the number
                ret = ret.subSequence(ret.indexOf('+') + 1, ret.length()).toString();
            }
        } catch (NumberParseException e) {
            Log.e(TAG, e.getMessage());
        }
//        Log.d(TAG, "toE164: in " + phoneNumber + " out " + ret);
        return ret;
    }

    public static boolean areEqualPhoneNumbers(Context context, String lhs, String rhs) {
        context = context.getApplicationContext();
        if (lhs.equals(rhs))
            return true;
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        String countryCode = DeviceUtils.getCountryCode(context);
        try {
            Phonenumber.PhoneNumber lhsPhone = util.parse(lhs, countryCode);
            Phonenumber.PhoneNumber rhsPhone = util.parse(rhs, countryCode);
            return lhsPhone.equals(rhsPhone);
        } catch (NumberParseException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }
}
