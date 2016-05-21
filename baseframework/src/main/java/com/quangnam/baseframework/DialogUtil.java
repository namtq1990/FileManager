package com.quangnam.baseframework;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by quangnam on 4/19/16.
 */
public class DialogUtil {
    public static ProgressDialog makeProgressDialog(Context context) {
        return makeProgressDialog(context, "");
    }

    public static ProgressDialog makeProgressDialog(Context context, String message) {
        return makeProgressDialog(context, message, false);
    }

    public static ProgressDialog makeProgressDialog(Context context, String message, boolean isCancelable) {
        return makeProgressDialog(context, message, isCancelable, false);
    }

    public static ProgressDialog makeProgressDialog(Context context,
                                                    String message,
                                                    boolean isCancelable,
                                                    boolean cancelOnTouchOutside) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(isCancelable);
        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
        dialog.setMessage(message);

        return dialog;
    }
}
