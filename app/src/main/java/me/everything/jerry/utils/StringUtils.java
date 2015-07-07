package me.everything.jerry.utils;

/**
 * Created by nitsan on 7/7/15.
 */
public class StringUtils {

    public static final String EMPTY_STRING = "";

    public static boolean isNullOrEmpty(String string) {
        if (null == string) {
            return true;
        }
        return string.isEmpty();
    }

}
