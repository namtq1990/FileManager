/*
 * MIT License
 *
 * Copyright (c) 2017 Tran Quang Nam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
