package com.tqnam.filemanager.explorer.fileExplorer;

import android.os.Bundle;
import android.text.TextUtils;

import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.explorer.ExplorerPresenter;
import com.tqnam.filemanager.model.ErrorCode;
import com.tqnam.filemanager.model.ExplorerModel;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.eventbus.LocalRefreshDataEvent;
import com.tqnam.filemanager.model.operation.BasicOperation;
import com.tqnam.filemanager.model.operation.CopyFileOperation;
import com.tqnam.filemanager.model.operation.DeleteOperation;
import com.tqnam.filemanager.model.operation.MoveOperation;
import com.tqnam.filemanager.model.operation.Operation;
import com.tqnam.filemanager.model.operation.Validator;
import com.tqnam.filemanager.utils.DefaultErrorAction;
import com.tqnam.filemanager.utils.FileUtil;
import com.tqnam.filemanager.utils.OperatorManager;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by tqnam on 10/28/2015.
 * Implement interface {@link ExplorerPresenter}
 */
public class FileExplorerPresenter implements ExplorerPresenter {
    private static final long TIME_LOADING_TRIGGER = 300;

    private View mView;
    private ExplorerModel mModel;
    private ActionSuccess mActionSuccess = new ActionSuccess();
    private DefaultErrorAction mErrorAction = new DefaultErrorAction() {

        @Override
        public void showErrorMessage(String message) {
            mView.showError(message);
        }

        @Override
        public void showErrorMessage(int stringID) {
            mView.showError(stringID);
        }
    };
    private ItemExplorer mCurFolder;
    private boolean mShowLoading;
    private Operation mValidatingOperation;

    public FileExplorerPresenter(ExplorerModel model) {
        mModel = model;
        mShowLoading = false;
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
    public void bind(View view) {
        mView = view;

        if (mShowLoading) {
            mView.showLoading(true);
        }
    }

    @Override
    public void unbind(View view) {
        if (mView == view)
            mView = null;
    }

    @Override
    public void onBackPressed() {
        if (mModel.mParentPath != null
                || !mModel.mCurLocation.equals(mView.getRootPath())) {
            FileItem parentFolder = new FileItem(mModel.mParentPath);
            openDirectory(parentFolder);
        }
    }

    // EventBus function
    @Subscribe
    public void onEvent(LocalRefreshDataEvent event) {
        reload();
    }
    //

    @Override
    public List<ItemExplorer> getListData() {
        return mModel.getList();
    }

    @Override
    public void openItem(ItemExplorer item) {

        if (item.isDirectory()) {
            openDirectory(item);
        } else {
            mView.openPreview(item);
        }

    }

    @Override
    public void openDirectory(final ItemExplorer item) {
        Observable<Void> observable = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
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
                            mModel.mParentPath = item.getParentPath();

                            mCurFolder = item;
                        }
                    } else if (getOpenOption() == OpenOption.SEARCH) {
                        if (mView != null) {
                            mView.replaceExplorerAtItem(item);
                            subscriber.onCompleted();
                            return;
                        }
                    }

                } else {
                    // User wrong function to open item, error may be in openFile() function
                    throw new SystemException(ErrorCode.RK_EXPLORER_OPEN_WRONG_FUNCTION,
                            "Wrong function to open");
                }

                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
        mapStream(observable, true, true).subscribe(mActionSuccess, mErrorAction);
    }

    @Override
    public void renameItem(final ItemExplorer item, final String newLabel) {

        Observable<Void> observable = Observable.create(
                new Observable.OnSubscribe<Void>() {

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

                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }

                }
        );
        mapStream(observable, true, true).subscribe(mActionSuccess, mErrorAction);
    }

    @Override
    public void reload() {
        if (getOpenOption() == OpenOption.EXPLORER) {
            openDirectory(mCurFolder);
        } else {
            queryFile(getCurLocation(), mView.getQuery());
        }
    }

    @Override
    public void createFile(final String filename) {
        Observable<FileItem> observable = Observable.create(new Observable.OnSubscribe<FileItem>() {
            @Override
            public void call(Subscriber<? super FileItem> subscriber) {
                File file = FileUtil.createFile(mModel.mCurLocation, filename);
                FileItem item = new FileItem(file.getPath());
                mModel.getList().add(item);
                subscriber.onNext(item);
                subscriber.onCompleted();
            }
        });
        observable = (Observable<FileItem>) mapStream(observable, true, true);

        observable.subscribe(new Action1<FileItem>() {
            @Override
            public void call(FileItem fileItem) {
                reload();
            }
        }, mErrorAction);
    }

    @Override
    public void createFolder(final String filename) {
        Observable<FileItem> observable = Observable.create(new Observable.OnSubscribe<FileItem>() {
            @Override
            public void call(Subscriber<? super FileItem> subscriber) {
                File file = FileUtil.createFolder(mModel.mCurLocation, filename);
                FileItem item = new FileItem(file.getPath());
                mModel.getList().add(item);
                subscriber.onNext(item);
                subscriber.onCompleted();
            }
        });
        observable = (Observable<FileItem>) mapStream(observable, true, true);

        observable.subscribe(new Action1<FileItem>() {
            @Override
            public void call(FileItem fileItem) {
                reload();
            }
        }, mErrorAction);
    }

    @Override
    public void queryFile(final String path, final String query) {
        Observable<Void> observable = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                if (TextUtils.isEmpty(query)) {
                    subscriber.onCompleted();
                    return;
                }

                List<ItemExplorer> list = FileUtil.search(path, query);
                //                        switch (getOpenOption()) {
                //                            case EXPLORER:
                //
                //                        }
                mModel.setList(list);
                mModel.sort();
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
        observable = (Observable<Void>) mapStream(observable, true, true);
        observable.subscribe(mActionSuccess, mErrorAction);
    }

    @Override
    public Operation<?> deleteOperation(List<ItemExplorer> list) {
        List<FileItem> listFile = (List<FileItem>) (List<? extends ItemExplorer>) list;
        DeleteOperation operation = new DeleteOperation(listFile);

        // TODO Check if operator is validated, so add to unvalidatedList
        mModel.getUnvalidatedList().add(operation);

        return operation;
    }

    @Override
    public Operation<?> copyCurFolderOperation(List<ItemExplorer> listSelected) {
        List<FileItem> listFile = (List<FileItem>) (List<? extends ItemExplorer>) listSelected;
        CopyFileOperation operator = new CopyFileOperation(listFile, mModel.mCurLocation);

        mModel.getUnvalidatedList().add(operator);

        return operator;
    }

    @Override
    public Operation<?> moveCurFolderOperation(List<ItemExplorer> listSelected) {
        List<FileItem> listFile = (List<FileItem>) (List<? extends ItemExplorer>) listSelected;
        MoveOperation operation = new MoveOperation(listFile, mModel.mCurLocation);

        mModel.getUnvalidatedList().add(operation);

        return operation;
    }

    @Override
    public Operation<?> doPasteAction() {
        List<ItemExplorer> listSelected = mModel.getClipboard();
        int category = mModel.getClipboardCategory();
        Operation operation = null;

        if (category == OperatorManager.CATEGORY_MOVE) {
            operation = moveCurFolderOperation(listSelected);
        } else if (category == OperatorManager.CATEGORY_COPY) {
            operation = copyCurFolderOperation(listSelected);
        }

        mModel.saveClipboard(null, category);

        trySetValidated(operation);

        return operation;
    }

    @Override
    public void trySetValidated(Operation operation) {
        if (operation instanceof BasicOperation) {
            if (operation.getData() == null
                    || ((List) operation.getData()).isEmpty()) {
                return;
            }

            Validator validator = ((BasicOperation) operation).getValidator();
            if (!validator.getListViolated().isEmpty()) {
                mView.showValidate(operation);
                return;
            }
        }

        int category = OperatorManager.CATEGORY_OTHER;
        if (operation instanceof DeleteOperation) {
            category = OperatorManager.CATEGORY_DELETE;
            mView.showMessage("Deleting...");
        } else if (operation instanceof MoveOperation) {
            category = OperatorManager.CATEGORY_MOVE;
            mView.showMessage("Moving...");
        } else if (operation instanceof CopyFileOperation) {
            category = OperatorManager.CATEGORY_COPY;
            mView.showMessage("Copying...");
        }

        mModel.getUnvalidatedList().remove(operation);
        mModel.getOperatorManager().addOperator(operation, category);

        operation.execute()
                .map(new Func1<Object, Operation.UpdatableData>() {

                    @Override
                    public Operation.UpdatableData call(Object o) {
                        return (Operation.UpdatableData) o;
                    }
                })
                .subscribe(new Action1<Operation.UpdatableData>() {
                    @Override
                    public void call(Operation.UpdatableData data) {
                        if (mCurFolder != null) openDirectory(mCurFolder);

                        if (data.isFinished()) {
                            if (mView != null)
                                mView.showMessage("Done!");
                        } else if (data.isError()) {
                            throw new SystemException(SystemException.RK_UNKNOWN, "Operation error");
                        }
                    }
                }, mErrorAction);
    }

    @Override
    public void saveClipboard(List<ItemExplorer> clipboard, int category) {
        mModel.saveClipboard(clipboard, category);
    }

    @Override
    public ArrayList<ItemExplorer> getClipboard() {
        return mModel.getClipboard();
    }

    @Override
    public Operation getValidatingOperation() {
        return mValidatingOperation;
    }

    @Override
    public void setValidatingOperation(Operation operation) {
        mValidatingOperation = operation;
    }

    @Override
    public String getCurLocation() {
        return mModel.mCurLocation;
    }

    @Override
    public void setCurLocation(String path) {
        mModel.mCurLocation = path;
        mCurFolder = new FileItem(path);
    }

    @Override
    public ItemExplorer getCurFolder() {
        if (mCurFolder != null)
            return mCurFolder;

        return new FileItem(getCurLocation());
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

    private Observable<?> mapStream(Observable<?> observable,
                                    final boolean useBackgroundThread,
                                    final boolean showLoading) {
        if (useBackgroundThread) {
            observable = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        if (showLoading) {
            Observable.timer(TIME_LOADING_TRIGGER, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<Long, Void>() {
                        @Override
                        public Void call(Long aLong) {
                            // After delay time, stream's still working. So show loading
                            if (mShowLoading) {
                                mView.showLoading(true);
                            }
                            return null;
                        }
                    })
                    .subscribe();

            observable = observable.doOnSubscribe(new Action0() {
                @Override
                public void call() {
                    mShowLoading = true;
                }
            })
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            mView.showLoading(false);
                            mShowLoading = false;
                        }
                    }).doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            mView.showLoading(false);
                            mShowLoading = false;
                        }
                    });
        }

        return observable;
    }

    private class ActionSuccess implements Action1<Object> {

        @Override
        public void call(Object o) {
            if (mView != null)
                mView.refreshView();
        }
    }

}
