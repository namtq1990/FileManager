package com.tqnam.filemanager.explorer;

import com.tqnam.filemanager.Application;

import java.io.File;

/**
 * Created by tqnam on 10/28/2015.
 */
public class FileExplorerPresenter implements ExplorerPresenter {

    private ExplorerView mView;
    private Application.GlobalData mDataStorage;

    public FileExplorerPresenter(ExplorerView view, Application.GlobalData data) {
        mView = view;
        mDataStorage = data;

        mView.init(this, mDataStorage.mListFile);
    }

    @Override
    public void openDirectory(ItemExplorer item) {
        FileItem folder = (FileItem) item;

        if (item != null && folder.isDirectory()) {
            File[] list = folder.listFiles();

            if (list != null) {
                mDataStorage.mCurFolder = item.getPath();
                mDataStorage.mListFile.clear();

                for (File file : list) {
                    mDataStorage.mListFile.add(new FileItem(file.getAbsolutePath()));
                }

                mView.updateList(mDataStorage.mListFile);
            }
            else {
                if (!folder.canRead())
                    mView.onErrorPermission();
            }
        }
    }

}
