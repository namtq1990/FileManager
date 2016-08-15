package com.quangnam.baseframework;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by quangnam on 4/19/16.
 */
public class DialogUtil {

    //----------------------------------------------------------------------------------------------
    //Progress dialog

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

    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    //AlertDialog

    public  static AlertDialog.Builder makeSelectDialog(Context context,
                                                        String title,
                                                        CharSequence[] listItem,
                                                        DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(listItem, onClickListener);

        return builder;
    }
    //----------------------------------------------------------------------------------------------
}
