package com.tqnam.filemanager.explorer.fileExplorer;

import android.os.Bundle;

import com.tqnam.filemanager.model.ExplorerModel;

import java.util.ArrayList;

/**
 * Created by quangnam on 11/7/15.
 */
public class FileModel extends ExplorerModel {

    public FileModel() {
        super();
        mListItem = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<FileItem> getList() {
        return (ArrayList) mListItem;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        ArrayList<FileItem> listFile = savedState.getParcelableArrayList(ExplorerModel.ARG_LIST_ITEM);
        mListItem = (ArrayList) listFile;
    }

    @Override
    public void onSavedInstanceState(Bundle savedState) {
        super.onSavedInstanceState(savedState);
        savedState.putParcelableArrayList(ExplorerModel.ARG_LIST_ITEM, getList());
    }
}
