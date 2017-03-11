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

package com.tqnam.filemanager.model;

import com.quangnam.base.exception.SystemException;

/**
 * Created by quangnam on 3/3/16.
 * Error code in program
 */
public class ErrorCode {

    /**
     * unknown error
     */
    public static final int RK_UNKNOWN = SystemException.RK_UNKNOWN;

    public static final int RK_IMAGE_LOADING_ERROR = 2;

    /**
     * Errorcode for user wrong function to open item explorer
     */
    public static final int RK_EXPLORER_OPEN_WRONG_FUNCTION = 100;

    public static final int RK_EXPLORER_OPEN_NOTHING = 101;

    public static final int RK_EXPLORER_OPEN_ERROR = 102;

    public static final int RK_EXPLORER_FILE_EXISTED = 103;

    public static final int RK_EXPLORER_PERMISSION = 104;

    public static final int RK_RENAME_ERR = 110;

    public static final int RK_COPY_ERR = 120;

    public static final int RK_MOVE_ERR = 130;

    //    public static String generateError(String message, int errorcode) {
//        return "" + errorcode + ":{" + message + "}";
//    }
//
//    public static int getErrorcode(String message) {
//        try {
//            String errorcode = message.split(":")[0];
//            return Integer.parseInt(errorcode);
//        } catch (Throwable throwable) {
//            return RK_UNKNOWN;
//        }
//    }
}