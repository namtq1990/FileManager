package com.tqnam.filemanager.explorer.fileExplorer;

import android.os.Bundle;
import android.text.TextUtils;

import com.quangnam.baseframework.exception.SystemException;
import com.quangnam.baseframework.utils.RxCacheWithoutError;
import com.squareup.picasso.Target;
import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.model.CopyFileOperator;
import com.tqnam.filemanager.model.DeleteOperator;
import com.tqnam.filemanager.model.ErrorCode;
import com.tqnam.filemanager.model.ExplorerModel;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.Operator;
import com.tqnam.filemanager.utils.DefaultErrorAction;
import com.tqnam.filemanager.utils.FileUtil;
import com.tqnam.filemanager.utils.OperatorManager;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by tqnam on 10/28/2015.
 * Implement interface {@link ExplorerPresenter}
 */
public class FileExplorerPresenter implements ExplorerPresenter {

    private View  mView;
    private ExplorerModel mModel;
    private Target mCurTarget;

    public FileExplorerPresenter(View view, ExplorerModel model) {
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
        if (mModel.mParentPath != null || !mModel.mCurLocation.equals(mView.getRootPath())) {
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
                            if (getOpenOption() == OpenOption.EXPLORER) {

                                List<FileItem> list = FileUtil.open((FileItem) item);

                                if (list != null) {
                                    mModel.mCurLocation = item.getPath();
                                    mModel.setList(list);
                                    mModel.sort();
                                    mModel.resetDisplayList();
                                    mModel.mParentPath = item.getParentPath();
                                }
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
    public Observable<Void> renameItem(final ItemExplorer item, final String newLabel) {

        return Observable.create(new Observable.OnSubscribe<Void>() {

            @Override
            public void call(Subscriber<? super Void> subscriber) {
                FileItem file = (FileItem) item;
                FileItem newFile = new FileItem(file.getParent(), newLabel);
                boolean isRenameSuccess = file.renameTo(newFile);

                if (!isRenameSuccess) {
                    throw new SystemException(ErrorCode.RK_RENAME_ERR, "Can't rename file " + item);
                }

                int index = mModel.getList().indexOf(file);
                if (index != -1) mModel.getList().set(index, newFile);
                index = mModel.getDisplayedItem().indexOf(file);
                if (index != -1) mModel.getDisplayedItem().set(index, newFile);

                subscriber.onNext(null);
                subscriber.onCompleted();
            }

        }
        ).compose(new RxCacheWithoutError<Void>(1));
    }

    @Override
    public Observable<ItemExplorer> reload() {
        FileItem file = new FileItem(mModel.mCurLocation);
        return openDirectory(file);
    }

    @Override
    public Observable<Void> createFile(final String filename) {
        return Observable.just(filename)
                .flatMap(new Func1<String, Observable<ItemExplorer>>() {
                    @Override
                    public Observable<ItemExplorer> call(String s) {
                        FileUtil.createFile(mModel.mCurLocation, filename);
                        return reload();
                    }
                })
                .map(new Func1<ItemExplorer, Void>() {
                    @Override
                    public Void call(ItemExplorer itemExplorer) {
                        return null;
                    }
                });
    }

    @Override
    public Observable<Void> createFolder(final String filename) {
        return Observable.just(filename)
                .flatMap(new Func1<String, Observable<ItemExplorer>>() {
                    @Override
                    public Observable<ItemExplorer> call(String s) {
                        FileUtil.createFolder(mModel.mCurLocation, filename);
                        return reload();
                    }
                })
                .map(new Func1<ItemExplorer, Void>() {
                    @Override
                    public Void call(ItemExplorer itemExplorer) {
                        return null;
                    }
                });
    }

    @Override
    public Observable<List<? extends ItemExplorer>> quickQueryFile(final String query) {
        return Observable.just(query)
                .map(new Func1<String, List<? extends ItemExplorer>>() {
                    @Override
                    public List<ItemExplorer> call(String s) {
                        mModel.resetDisplayList();
                        FileUtil.filter(mModel.getDisplayedItem(), s);

                        return mModel.getDisplayedItem();
                    }
                });
    }

    @Override
    public Observable<List<? extends ItemExplorer>> quickQueryFile(final String query, final String path) {
        if (path.equals(mModel.mCurLocation)) {
            return quickQueryFile(query);
        }

        return Observable.just(query)
                .map(new Func1<String, List<? extends ItemExplorer>>() {
                    @Override
                    public List<? extends ItemExplorer> call(String query) {
                        List<FileItem> list = FileUtil.open(path);
                        FileUtil.filter(list, query);

                        return list;
                    }
                });
    }

    @Override
    public Observable<List<ItemExplorer>> queryFile(final String path, final String query) {
        return Observable.just(query)
                .map(new Func1<String, List<ItemExplorer>>() {
                    @Override
                    public List<ItemExplorer> call(String s) {
                        if (TextUtils.isEmpty(query))
                            return null;

                        List<ItemExplorer> list = FileUtil.search(path, query);
//                        switch (getOpenOption()) {
//                            case EXPLORER:
//
//                        }
                        mModel.setList(list);
                        mModel.resetDisplayList();

                        return list;
                    }
                });
    }

    @Override
    public Operator<?> deleteOperator(List<ItemExplorer> list) {
        List<FileItem> listFile = (List<FileItem>) (List<? extends ItemExplorer>) list;
        DeleteOperator operation = new DeleteOperator(listFile);

        // TODO Check if operator is validated, so add to unvalidatedList
        mModel.getUnvalidatedList().add(operation);

        return operation;
    }

    @Override
    public Operator<?> copyCurFolderOperator(List<ItemExplorer> listSelected) {
        List<FileItem> listFile = (List<FileItem>) (List<? extends ItemExplorer>) listSelected;
        CopyFileOperator operator = new CopyFileOperator(listFile, mModel.mCurLocation);

        mModel.getUnvalidatedList().add(operator);

        return operator;
    }

    @Override
    public void setValidated(Operator operator) {
        int category = OperatorManager.CATEGORY_OTHER;
        if (operator instanceof DeleteOperator) {
            category = OperatorManager.CATEGORY_DELETE;
            mView.showMessage("Deleting...");
        } else if (operator instanceof CopyFileOperator) {
            category = OperatorManager.CATEGORY_COPY;
            mView.showMessage("Copying...");
        }

        mModel.getUnvalidatedList().remove(operator);
        mModel.getOperatorManager().addOperator(operator, category);

        operator.execute()
                .concatWith(openDirectory(getCurFolder()))
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        mView.refreshView();
                    }
                }, new DefaultErrorAction() {

                    @Override
                    public void showErrorMessage(String message) {
                        mView.showError(message);
                    }

                    @Override
                    public void showErrorMessage(int stringID) {
                        mView.showError(stringID);
                    }
                });
    }

    @Override
    public void saveClipboard(List<ItemExplorer> clipboard) {
        mModel.saveClipboard(clipboard);
    }

    @Override
    public ArrayList<ItemExplorer> getClipboard() {
        return mModel.getClipboard();
    }

    @Override
    public String getCurLocation() {
        return mModel.mCurLocation;
    }

    @Override
    public void setCurLocation(String path) {
        mModel.mCurLocation = path;
    }

    @Override
    public ItemExplorer getCurFolder() {
        return new FileItem(getCurLocation());
    }

    @Override
    public int getItemDisplayCount() {
        return mModel.getDisplayCount();
    }

    @Override
    public ItemExplorer getItemDisplayedAt(int position) {
        return mModel.getItemDisplayedAt(position);
    }

    @Override
    public OpenType getOpenType() {
        return OpenType.LOCAL;
    }

    @Override
    public void setOpenType(OpenType openType) {
        //TODO implements
    }

    @Override
    public OpenOption getOpenOption() {
        return mView.getQuery() == null ? OpenOption.EXPLORER : OpenOption.SEARCH;
    }

    @Override
    public void setOpenOption(OpenOption openOption) {
        // TODO implements
    }

}
