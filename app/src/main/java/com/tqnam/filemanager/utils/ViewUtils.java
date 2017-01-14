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
