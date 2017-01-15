package com.quangnam.baseframework;

import java.util.HashMap;

/**
 * Created by quangnam on 11/17/16.
 * Project FileManager-master
 */
public class TrackingTime {
    private static final HashMap<String, Long> mValue = new HashMap<>();

    public static void beginTracking(String tag) {
        long curTime = System.currentTimeMillis();
        mValue.put(formatKey(tag, true), curTime);
    }

    /**
     * Return tracking time task in millisecond.
     */
    public static long endTracking(String tag) {
        String key = formatKey(tag, true);
        Long object = mValue.get(key);
        long oldTime;
        if (object == null) {
            Log.e("This " + tag + " never started tracking, so set tracking time to current time");
            oldTime = System.currentTimeMillis();
        } else {
            oldTime = object;
        }
        long curTime = System.currentTimeMillis();

        mValue.remove(key);

        return (curTime - oldTime);
    }

    private static String formatKey(String tag, boolean isStart) {
        return tag + (isStart ? "_begin" : "_end");
    }
}
