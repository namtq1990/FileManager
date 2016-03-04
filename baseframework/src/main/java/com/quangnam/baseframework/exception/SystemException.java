package com.quangnam.baseframework.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by quangnam on 3/4/16.
 * Common exception for all system
 */
public class SystemException extends RuntimeException {
    public int mErrorcode;
    public String mMessage;

    public SystemException(int errorcode, String message) {
        this(errorcode, message, null);
    }

    public SystemException(int errorcode, String message, Throwable throwable) {
        super(message, throwable);

        mErrorcode = errorcode;
        mMessage = message;
    }

    @Override
    public String toString() {
        return "Errorcode: " + mErrorcode + "\n" + super.toString();
    }

    @Override
    public void printStackTrace() {
        System.err.println("Custom exception with errorcode "
                + mErrorcode);
        System.err.println(mMessage);
        super.printStackTrace();
    }

    public static class SystemExceptionAdditionalInformation extends SystemException {
        private Map<String, Object> mExtraData;

        public SystemExceptionAdditionalInformation(int errorcode, String message) {
            this(errorcode, message, null);
        }

        public SystemExceptionAdditionalInformation(int errorcode,
                                                    String message,
                                                    Throwable throwable) {
            super(errorcode, message, throwable);
            mExtraData = new HashMap<>();
        }

        public void put(String key, Object data) {
            mExtraData.put(key, data);
        }

        public Object get(String key) {
            return mExtraData.get(key);
        }

        public boolean containsKey(String key) {
            return mExtraData.containsKey(key);
        }
    }
}
