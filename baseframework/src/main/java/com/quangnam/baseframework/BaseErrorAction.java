package com.quangnam.baseframework;

import com.quangnam.baseframework.exception.SystemException;

import rx.functions.Action1;

/**
 * Created by quangnam on 11/17/16.
 * Project FileManager-master
 */
public abstract class BaseErrorAction implements Action1<Throwable> {
    @Override
    public void call(Throwable e) {
        SystemException exception;
        if (!(e instanceof SystemException)) {
            exception = new SystemException(SystemException.RK_UNKNOWN, "", e);
        } else {
            exception = (SystemException) e;
        }

        exception.printStackTrace();
        int errCode = exception.mErrorcode;

        onError(errCode, exception);
    }

    public abstract void onError(int errCode, SystemException e);

    public abstract void showErrorMessage(String message);
}
