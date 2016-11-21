package com.tqnam.filemanager.explorer.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.quangnam.baseframework.BaseDialog;

/**
 * Created by quangnam on 11/19/16.
 * Project FileManager-master
 */
public class AlertDialogFragment extends BaseDialog {
    public static final String TAG = AlertDialogFragment.class.getName();
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_ACTION = "action";

    private AlertDialogListener mListener;
    private DialogInterface.OnClickListener mOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mListener != null) {
                mListener.onDialogClick(dialog, which, getAction(), getArguments());
            }
        }
    };

    public static AlertDialogFragment newInstance(int action, String message) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MESSAGE, message);
        bundle.putInt(ARG_ACTION, action);
        fragment.setArguments(bundle);

        return fragment;
    }

    public void setListener(AlertDialogListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(getMessage())
                .setPositiveButton(getPositiveLabel(), mOnClick)
                .setNegativeButton(getNegativeLabel(), mOnClick)
                .setNeutralButton("Ignore", mOnClick);

        return builder.create();
    }

    public String getMessage() {
        return getArguments().getString(ARG_MESSAGE);
    }

    public int getAction() {
        return getArguments().getInt(ARG_ACTION);
    }

    public String getPositiveLabel() {
        return getString(android.R.string.ok);
    }

    public String getNegativeLabel() {
        return getString(android.R.string.cancel);
    }

    public interface AlertDialogListener {
        void onDialogClick(DialogInterface dialog, int which, int action, Bundle args);
    }
}
