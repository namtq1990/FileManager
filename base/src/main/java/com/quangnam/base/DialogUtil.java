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
