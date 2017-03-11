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

package com.tqnam.filemanager.explorer.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.quangnam.base.BaseDialog;
import com.tqnam.filemanager.R;

/**
 * Created by quangnam on 11/19/16.
 * Project FileManager-master
 */
public class AlertDialogFragment extends BaseDialog {
    public static final String TAG = AlertDialogFragment.class.getName();
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_ACTION = "action";
    private static final String ARG_POSITIVE = "positive";
    private static final String ARG_NEUTRAL = "neutral";
    private static final String ARG_NEGATIVE = "negative";

    private AlertDialogListener mListener;
    private DialogInterface.OnClickListener mOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mListener != null) {
                mListener.onDialogClick(dialog, which, getAction(), getArguments());
            }
        }
    };

    public static AlertDialogFragment newInstance(int action,
                                                  String message,
                                                  String btnPos,
                                                  String btnNeutral,
                                                  String btnNegative) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MESSAGE, message);
        bundle.putInt(ARG_ACTION, action);
        bundle.putString(ARG_POSITIVE, btnPos);
        bundle.putString(ARG_NEUTRAL, btnNeutral);
        bundle.putString(ARG_NEGATIVE, btnNegative);

        fragment.setArguments(bundle);

        return fragment;
    }

    public void setListener(AlertDialogListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog)
                .setMessage(getMessage());
        if (getNeutralLabel() != null)
                builder.setNeutralButton("Ignore", mOnClick);
        if (getPositiveLabel() != null) {
            builder.setPositiveButton(getPositiveLabel(), mOnClick);
        }
        if (getNegativeLabel() != null) {
            builder.setNegativeButton(getNegativeLabel(), mOnClick);
        }

        return builder.create();
    }

    public String getMessage() {
        return getArguments().getString(ARG_MESSAGE);
    }

    public int getAction() {
        return getArguments().getInt(ARG_ACTION);
    }

    public String getPositiveLabel() {
        return getArguments().getString(ARG_POSITIVE);
    }

    public String getNegativeLabel() {
        return getArguments().getString(ARG_NEGATIVE);
    }

    public String getNeutralLabel() {
        return getArguments().getString(ARG_NEUTRAL);
    }

    public interface AlertDialogListener {
        void onDialogClick(DialogInterface dialog, int which, int action, Bundle args);
    }
}
