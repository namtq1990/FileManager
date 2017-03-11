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

package com.quangnam.base;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

    public static BufferedReader loadLog() throws IOException {
        String[] cmd = {
                "logcat",
                "-d",
                "-v",
                "threadtime"
        };

        Process process = Runtime.getRuntime().exec(cmd);

        return new BufferedReader(new InputStreamReader(
                process.getInputStream()
        ));
    }
}
