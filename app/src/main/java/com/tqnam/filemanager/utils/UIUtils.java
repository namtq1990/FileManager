package com.tqnam.filemanager.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UIUtils {

    public static void hideKeyboard(Activity activity) {
        View curFocus = activity.getCurrentFocus();

        if (curFocus != null) {
            InputMethodManager im = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(curFocus.getWindowToken(), 0);
        }
    }
}
