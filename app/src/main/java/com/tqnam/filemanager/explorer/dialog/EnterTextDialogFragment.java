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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.quangnam.baseframework.BaseDialog;
import com.tqnam.filemanager.R;

/**
 * Created by quangnam on 11/3/16.
 */
public class EnterTextDialogFragment extends BaseDialog {
    public static final String TAG = EnterTextDialogFragment.class.getSimpleName();

    private static final String ARG_TITLE = "title";
    private static final String ARG_LABEL = "label";
    private static final String ARG_KEY = "key";

    private EditText mEdContent;
    private EnterTextDialogListener mListener;

    public static EnterTextDialogFragment newInstance(String title, String label, String key) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_LABEL, label);
        args.putString(ARG_KEY, key);
        
        EnterTextDialogFragment fragment = new EnterTextDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EnterTextDialogListener) {
            mListener = (EnterTextDialogListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_entertext, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setMessage(getArguments().getString(ARG_LABEL))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onSubmit(getKey(), mEdContent.getText().toString());
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onCancel(getKey(), mEdContent.getText().toString());
                        }
                    }
                })
                .setView(rootView);

        mEdContent = (EditText) rootView.findViewById(R.id.ed_content);

        return builder.create();
    }

    public String getKey() {
        return getArguments().getString(ARG_KEY);
    }

    public interface EnterTextDialogListener {
        void onSubmit(String key, String content);
        void onCancel(String key, String content);
    }

}
