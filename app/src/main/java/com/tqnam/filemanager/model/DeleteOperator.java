package com.tqnam.filemanager.model;

import com.tqnam.filemanager.explorer.fileExplorer.FileItem;

import java.util.List;

import rx.Observable;

/**
 * Created by quangnam on 11/18/16.
 * Project FileManager-master
 */
public class DeleteOperator extends Operator.TraverseFileOperator<FileItem> {

    public DeleteOperator(List<FileItem> data) {
        super(data);
    }

    @Override
    public Operator createStreamFromData(FileItem data) {
        return null;
    }

    @Override
    public Observable<?> execute(Object... arg) {
        return null;
    }

    @Override
    public String getSourcePath() {
        return null;
    }

    @Override
    public String getDestinationPath() {
        return null;
    }
}
