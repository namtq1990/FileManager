package com.tqnam.filemanager.utils;

import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.CopyFileOperator;

import java.util.List;

/**
 * Created by quangnam on 11/17/16.
 * Project FileManager-master
 */
public class OperatorBuilder {

    public static CopyFileOperator makeCopy(List<FileItem> data, String path) {
        return new CopyFileOperator(data, path);
    }

//    public static CopyFileOperator.SingleFileCopyOperator makeCopy() {
//        return new CopyFileOperator.SingleFileCopyOperator()
//    }
}
