package com.tqnam.filemanager.utils;

import android.content.Context;
import android.widget.Toast;

import com.quangnam.baseframework.BaseErrorAction;
import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.Common;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.model.ErrorCode;

/**
 * Created by quangnam on 11/18/16.
 * Project FileManager-master
 */
public class DefaultErrorAction extends BaseErrorAction {
    @Override
    public void onError(int errCode, SystemException e) {
        e.printStackTrace();

        switch (errCode) {
            case ErrorCode.RK_EXPLORER_OPEN_ERROR:
                showErrorMessage(R.string.explorer_err_permission);
                break;
            case ErrorCode.RK_RENAME_ERR:
                showErrorMessage("Can't rename file, check permission");
                break;
            case ErrorCode.RK_EXPLORER_OPEN_NOTHING:
            case ErrorCode.RK_EXPLORER_OPEN_WRONG_FUNCTION:
                Common.Log("Calling wrong function");
            case ErrorCode.RK_UNKNOWN:
                showErrorMessage(R.string.error_unknown);
                break;
        }
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showErrorMessage(int stringID) {
        Context context = getContext();
        String s = context != null ? context.getString(stringID) : ("") + stringID;

        showErrorMessage(s);
    }

    public Context getContext() {
        return null;
    }
}
