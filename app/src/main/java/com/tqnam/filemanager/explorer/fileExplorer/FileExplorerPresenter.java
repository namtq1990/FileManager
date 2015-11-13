package com.tqnam.filemanager.explorer.fileExplorer;

import android.os.Bundle;

import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.explorer.ExplorerView;
import com.tqnam.filemanager.model.ExplorerModel;
import com.tqnam.filemanager.model.ItemExplorer;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tqnam on 10/28/2015.
 */
public class FileExplorerPresenter implements ExplorerPresenter {

    private ExplorerView mView;
    private ExplorerModel mModel;

    public FileExplorerPresenter(ExplorerView view, ExplorerModel model) {
        mView = view;
        mModel = model;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mModel.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        mModel.onSavedInstanceState(bundle);
    }

    @Override
    public void onBackPressed() {
        if (mModel.mParentPath != null) {
            FileItem parentFolder = new FileItem(mModel.mParentPath);
            openDirectory(parentFolder);
        }
    }

    @Override
    public void openDirectory(ItemExplorer item) {
        FileItem folder = (FileItem) item;

        if (item != null && folder.isDirectory()) {
            File[] list = folder.listFiles();

            if (list != null) {
                mModel.mCurLocation = item.getPath();
                mModel.mListItem.clear();

                for (File file : list) {
                    mModel.mListItem.add(new FileItem(file.getAbsolutePath()));
                }

                mModel.sort();
                mModel.mParentPath = item.getParentPath();

                mView.updateList(mModel.mListItem);
            }
            else {
                if (!folder.canRead())
                    mView.onErrorPermission();
            }
        }
    }

    @Override
    public ArrayList<? extends ItemExplorer> getCurList() {
        return mModel.mListItem;
    }

}
