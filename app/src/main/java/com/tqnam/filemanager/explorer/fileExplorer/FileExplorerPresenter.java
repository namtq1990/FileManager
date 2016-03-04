package com.tqnam.filemanager.explorer.fileExplorer;

import android.os.Bundle;

import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.explorer.ExplorerView;
import com.tqnam.filemanager.model.ErrorCode;
import com.tqnam.filemanager.model.ExplorerModel;
import com.tqnam.filemanager.model.ItemExplorer;

import java.io.File;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by tqnam on 10/28/2015.
 * Implement interface {@link ExplorerPresenter}
 */
public class FileExplorerPresenter implements ExplorerPresenter {

    private ExplorerView  mView;
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
    public Observable<ItemExplorer> onBackPressed() {
        if (mModel.mParentPath != null) {
            FileItem parentFolder = new FileItem(mModel.mParentPath);
            return openDirectory(parentFolder);
        }

        return null;
    }

    @Override
    public Observable<ItemExplorer> openItem(int position) {
        ItemExplorer item = getItemAt(position);

        if (item.isDirectory()) {
            return openDirectory(item);
        } else {
            return Observable.just(item);
        }
    }

    @Override
    public Observable<ItemExplorer> openDirectory(ItemExplorer item) {

        return Observable.just(item)
                .doOnNext(new Action1<ItemExplorer>() {
                    @Override
                    public void call(ItemExplorer item) {
                        FileItem folder = (FileItem) item;

                        if (item == null) {
                            throw new SystemException(ErrorCode.RK_EXPLORER_OPEN_NOTHING,
                                    "Nothing to open");
                        }

                        if (folder.isDirectory()) {
                            File[] list = folder.listFiles();

                            if (list != null) {
                                mModel.mCurLocation = item.getPath();
                                mModel.mListItem.clear();

                                for (File file : list) {
                                    mModel.mListItem.add(new FileItem(file.getAbsolutePath()));
                                }

                                mModel.sort();
                                mModel.mParentPath = item.getParentPath();
                            } else {
                                throw new SystemException(ErrorCode.RK_EXPLORER_OPEN_ERROR,
                                        "Cannot open folder " + item + ", check permission");
                            }

                        } else {
                            // User wrong function to open item, error may be in openFile() function
                            throw new SystemException(ErrorCode.RK_EXPLORER_OPEN_WRONG_FUNCTION,
                                    "Wrong function to open");
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mModel.getTotalItem();
    }

    @Override
    public ItemExplorer getItemAt(int position) {
        return mModel.getItemAt(position);
    }

}
