package com.tqnam.filemanager.model;

/**
 * Created by quangnam on 3/3/16.
 * Error code in program
 */
public class ErrorCode {

    /**
     * unknown error
     */
    public static final int RK_UNKNOWN = 1;

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