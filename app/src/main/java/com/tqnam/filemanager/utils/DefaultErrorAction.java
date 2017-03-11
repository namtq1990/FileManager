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

import android.content.Context;
import android.widget.Toast;

import com.quangnam.base.BaseErrorAction;
import com.quangnam.base.exception.SystemException;
import com.tqnam.filemanager.Application;
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
        return Application.getInstance();
    }
}
