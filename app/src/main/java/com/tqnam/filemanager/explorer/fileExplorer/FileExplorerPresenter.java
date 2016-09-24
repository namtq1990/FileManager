package com.tqnam.filemanager.explorer.fileExplorer;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.quangnam.baseframework.exception.SystemException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.explorer.ExplorerView;
import com.tqnam.filemanager.model.ErrorCode;
import com.tqnam.filemanager.model.ExplorerModel;
import com.tqnam.filemanager.model.ItemExplorer;

import java.io.File;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by tqnam on 10/28/2015.
 * Implement interface {@link ExplorerPresenter}
 */
public class FileExplorerPresenter implements ExplorerPresenter {

    private ExplorerView  mView;
    private ExplorerModel mModel;
    private Target mCurTarget;

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
        ItemExplorer item = getItemDisplayedAt(position);

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
                                mModel.clearItem();

                                for (File file : list) {
                                    mModel.addItem(new FileItem(file.getAbsolutePath()));
                                }

                                mModel.sort();
                                mModel.mParentPath = item.getParentPath();

                                mModel.resetDisplayList();
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
    public Observable<Bitmap> loadImage(ItemExplorer item) {
        final PublishSubject<Bitmap> observable = PublishSubject.create();
        Uri uri = item.getUri();
        mCurTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mCurTarget = null;
                observable.onNext(bitmap);
                observable.onCompleted();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                observable.onError(new SystemException(ErrorCode.RK_IMAGE_LOADING_ERROR,
                        "Image loading error"));
                mCurTarget = null;
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };

        Picasso.with(mView.getContext().getApplicationContext())
                .load(uri)
                .into(mCurTarget);

        return observable.cache(1);
    }

    @Override
    public Observable<Void> renameItem(final ItemExplorer item, final String newLabel) {

        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                FileItem file = (FileItem) item;
                File newFile = new File(file.getParent(), newLabel);
                boolean isRenameSuccess = ((FileItem) item).renameTo(newFile);

                if (!isRenameSuccess) {
                    throw new SystemException(ErrorCode.RK_RENAME_ERR, "Cann't rename file " + item);
                }

                return null;
            }
        });
    }

    @Override
    public Observable<ItemExplorer> reload() {
        FileItem file = new FileItem(mModel.mCurLocation);
        return openDirectory(file);
    }

    @Override
    public Observable<Void> quickQueryFile(final String query) {
        return Observable.just(query)
                .map(new Func1<String, Void>() {
                    @Override
                    public Void call(String s) {

                        if (s.equals("")) {
                            mModel.resetDisplayList();
                            return null;
                        } else {
                            mModel.clearDisplayItem();

                            for (int i = 0;i < mModel.getTotalItem();i++) {
                                ItemExplorer item = mModel.getItemAt(i);
                                if (item.getDisplayName().contains(query))
                                    mModel.addDisplayItem(item);
                            }
                        }

                        return null;
                    }
                });
    }

    @Override
    public Observable<Void> queryFile(String query) {
        return null;
    }

    @Override
    public int getItemDisplayCount() {
        return mModel.getDisplayCount();
    }

    @Override
    public ItemExplorer getItemDisplayedAt(int position) {
        return mModel.getItemDisplayedAt(position);
    }

}
