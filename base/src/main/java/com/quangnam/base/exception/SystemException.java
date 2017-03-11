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

package com.quangnam.base.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by quangnam on 3/4/16.
 * Common exception for all system
 */
public class SystemException extends RuntimeException {

    public static final int RK_UNKNOWN = 1;

    public int mErrorcode;
    public String mMessage;

    public SystemException(String message) {
        this(RK_UNKNOWN, message);
    }

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
