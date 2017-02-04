package com.quangnam.baseframework;

import android.content.Context;

/**
 * Created by quangnam on 4/18/16.
 */
public class Log {
    private static String TAG;

    static void init(Context context) {
        TAG = context.getPackageName();
    }

    public static void d(String message) {
        d(TAG, message);
    }

    public static void e(String message) {
        e(TAG, message);
    }

    public static void i(String message) {
        i(TAG, message);
    }

    public static void v(String message) {
        v(TAG, message);
    }

    public static void v(String tag, String message) {
        android.util.Log.v(tag, message);
    }

    public static void i(String tag, String message) {
        android.util.Log.i(tag, message);
    }

    public static void e(String tag, String message) {
        android.util.Log.e(tag, message);
    }

    public static void d(String tag, String message) {
        d(tag, message, false);
    }

    public static void d(String tag, String message, boolean dumpStack) {
        if (dumpStack) {
            Thread.dumpStack();
        }

        android.util.Log.d(tag, message);
    }
}
