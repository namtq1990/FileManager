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

package com.tqnam.filemanager.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class ViewUtils {

    public static void hideKeyboard(Activity activity) {
        View curFocus = activity.getCurrentFocus();

        if (curFocus != null) {
            InputMethodManager im = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(curFocus.getWindowToken(), 0);
        }
    }

    public static String getViewPagerTag(final int viewId, final long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    public static void formatTitleAndContentTextView(final TextView tv,
                                                     final String label,
                                                     final String content,
                                                     final Object[] labelSpan,
                                                     final Object[] contentSpan) {
        String text = label + content;
        SpannableString spanText = new SpannableString(text);
        if (labelSpan != null) {
            for (Object o : labelSpan) {
                spanText.setSpan(o, 0, label.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        if (contentSpan != null) {
            for (Object o : contentSpan) {
                spanText.setSpan(o, label.length(), text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }

        tv.setText(spanText);
    }

    /**
     * Lock view to don't allow user quickly action before app execute, such as double tap button,...
     */
    public static void disableUntilProcess(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, view.getResources().getInteger(android.R.integer.config_shortAnimTime));
    }

    /**
     * @see ViewUtils#disableUntilProcess(View)
     */
    public static void disableUntilProcess(final MenuItem item) {
        item.setEnabled(false);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                item.setEnabled(true);
            }
        }, 200);
    }

}
