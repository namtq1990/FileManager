package com.tqnam.filemanager.model.operation;

import com.quangnam.baseframework.Log;
import com.quangnam.baseframework.TrackingTime;
import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.ErrorCode;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.eventbus.LocalRefreshDataEvent;
import com.tqnam.filemanager.model.operation.propertyView.BasicOperationPropertyView;
import com.tqnam.filemanager.utils.DefaultErrorAction;
import com.tqnam.filemanager.utils.FileUtil;
import com.tqnam.filemanager.utils.OperationManager;

import org.greenrobot.eventbus.EventBus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
 * Created by quangnam on 11/16/16.
 * Project FileManager-master
 */
public class CopyFileOperation extends BasicOperation<FileItem> implements Validator.ValidateAction {
    public static final String TAG = CopyFileOperation.class.getCanonicalName();

    private HashMap<String, String> mDestinationPathStorage;
    private String mDestinationPath;
    private String mSourcePath;

    public CopyFileOperation(List<FileItem> data, String destPath) {
        super(data);

        if (data == null || data.isEmpty()) {
            throw new SystemException("List data is empty. Operation is aborted");
        }

        mDestinationPath = destPath;
        mSourcePath = data.get(0).getParentPath();
        mDestinationPathStorage = new HashMap<>();
        super.mPropertyView = new BasicOperationPropertyView(this);

        mResult.setOperatorHashcode(hashCode());
        getValidator().setValidateAction(this);

        validate();
    }

    @Override
    public void validate() {
        for (FileItem file : getData()) {
            String destinationPath = file.getPath().replaceFirst(mSourcePath, mDestinationPath);
            mDestinationPathStorage.put(file.getPath(), destinationPath);
            getValidator().validate(file);
        }
    }

    @Override
    public Observable<? extends BasicUpdatableData> createExecuter() {
        TrackingTime.beginTracking(formatTag());
        traverse();

        mCurObservable = Observable.interval(UPDATE_TIME, TimeUnit.MILLISECONDS)
                .takeUntil(
                        Observable.create(new Observable.OnSubscribe<Void>() {
                            @Override
                            public void call(Subscriber<? super Void> subscriber) {
                                execute();
                                subscriber.onCompleted();
                            }
                        })
                                .subscribeOn(Schedulers.io())
                )
                .concatWith(Observable.just(0L))
                .map(new Func1<Long, BasicUpdatableData>() {
                    @Override
                    public BasicUpdatableData call(Long aLong) {
                        long time = TrackingTime.endTracking(formatTag());
                        mResult.setSpeed(mResult.getSizeExecuted() - (getLastEmitSize()) * 1000 / (float) (time == 0 ? UPDATE_TIME : time));
                        setLastEmitSize(mResult.getSizeExecuted());
                        mResult.validate();
                        TrackingTime.beginTracking(formatTag());

                        return mResult;
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        TrackingTime.endTracking(formatTag());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        return mCurObservable;
    }

    private void execute() {
        try {
            Log.d("Copying...");

            ArrayList<Operation> list = getAllStream();

            for (Operation operation : list) {
                if (isCancelled()) {
                    break;
                }
                SingleFileCopyOperation copyOperator = (SingleFileCopyOperation) operation;
                copyOperator.execute(isOverwrite());
            }

            mResult.setError(false);
        } catch (Throwable e) {
            e.printStackTrace();
            mResult.setError(true);

            throw e;
        }
    }

    @Override
    public int getCategory() {
        return OperationManager.CATEGORY_COPY;
    }

    @Override
    public String getSourcePath() {
        return mSourcePath;
    }

    @Override
    public String getDestinationPath() {
        return mDestinationPath;
    }

    @Override
    public Operation createStreamFromData(FileItem data) {
        String destinationPath;
        String parentPath = data.getParentPath();
        String parentDestinationPath = mDestinationPathStorage.get(parentPath);
        if (parentDestinationPath != null) {
            destinationPath = parentDestinationPath + "/" + data.getDisplayName();
        } else {
            destinationPath = data.getPath().replaceFirst(mSourcePath, mDestinationPath);
        }

        mDestinationPathStorage.put(data.getPath(), destinationPath);

        if (!data.isDirectory()) {
            mResult.setSizeTotal(mResult.getSizeTotal() + data.length());
        }

        SingleFileCopyOperation operator = new SingleFileCopyOperation(data);
        operator.setDestination(destinationPath);

        return operator;
    }

    @Override
    public void revert() {
        super.revert();

        ArrayList<FileItem> deleteData = new ArrayList<>();

        ArrayList<Operation> listOperation = getAllStream();
        for (Operation operation : listOperation) {
            SingleFileCopyOperation copyFileOperation = (SingleFileCopyOperation) operation;
            if (!copyFileOperation.mExecuted) {
                continue;
            }
            if (copyFileOperation.mDestination.isDirectory()
                    && copyFileOperation.mFileExists) {
                continue;
            }

            deleteData.add(copyFileOperation.mDestination);
        }

        DeleteOperation deleteOperation = new DeleteOperation(deleteData);
        deleteOperation.execute()
                .subscribe(new Action1<BasicUpdatableData>() {
                    @Override
                    public void call(BasicUpdatableData basicUpdatableData) {
                        EventBus.getDefault().post(new LocalRefreshDataEvent());
                    }
                }, new DefaultErrorAction());

    }

    @Override
    public int validate(ItemExplorer item) {
        int mode = 0;
        String destinationPath = mDestinationPathStorage.get(item.getPath());

        if (destinationPath != null) {
            FileItem destFile = new FileItem(destinationPath);
            if (destFile.getPath().equals(item.getPath())) {
                mode = getValidator().setModeViolated(Validator.MODE_SAME_FILE, mode, true);
            } else if (destFile.exists()) {
                mode = getValidator().setModeViolated(Validator.MODE_FILE_EXIST, mode, true);
            }
            if ((destFile.exists() && !destFile.canWrite())
                    || (!destFile.exists() && !destFile.getParentItem().canWrite())) {
                mode = getValidator().setModeViolated(Validator.MODE_PERMISSION, mode, true);
            }
        }

        return mode;
    }

    public class SingleFileCopyOperation extends SingleFileOperation<FileItem> implements IRevert {

        private FileItem mDestination;
        private boolean mExecuted;
        private boolean mFileExists;

        public SingleFileCopyOperation(FileItem data) {
            super(data);

            mExecuted = false;
            Log.d("make operation for item: " + data.getAbsolutePath());
        }

        @Override
        public Observable execute(Object... arg) {
            return null;
        }

        private void execute(boolean isOverwrite) {
            if (mDestination.exists() && !isOverwrite) {
                throw new SystemException(ErrorCode.RK_EXPLORER_FILE_EXISTED,
                        "Can't copy file because file " + mDestination + " existed");
            }
            FileItem source = getData();

            mFileExists = mDestination.exists();

            if (source.isDirectory()) {
                if (!mDestination.exists()) {
                    boolean isMakeDir = mDestination.mkdir();

                    if (!isMakeDir) {
                        int errCode = ErrorCode.RK_COPY_ERR;

                        if (!mDestination.exists()) {
                            if (!mDestination.canWrite()) {
                                errCode = ErrorCode.RK_EXPLORER_PERMISSION;
                            }

                            throw new SystemException(errCode,
                                    "Cann't copy directory " + mDestination.getPath(),
                                    new IOException());
                        }
                    } else {
                        mExecuted = true;
                    }
                }
            } else {
                FileInputStream inputStream = null;
                FileOutputStream outputStream = null;

                try {
                    inputStream = new FileInputStream(source);
                    outputStream = new FileOutputStream(mDestination);
                    byte[] buff = new byte[FileUtil.BUFF_SIZE];
                    int byteRead;

                    mExecuted = true;
                    while ((byteRead = inputStream.read(buff, 0, buff.length)) > 0) {
                        outputStream.write(buff);
                        mResult.setSizeExecuted(mResult.getSizeExecuted() + byteRead);

                        if (isCancelled()) {
                            Log.d("Operation " + this + " cancelled.");
                            break;
                        }

                        performLockIfOperationPaused();
                    }

                    Log.d("COmpleted " + mDestination);
                } catch (Exception e) {
                    throw new SystemException(ErrorCode.RK_COPY_ERR, "Error while copying file", e);
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (Exception e) {
                        throw new SystemException(ErrorCode.RK_COPY_ERR, "Error while close stream", e);
                    }
                }
            }
        }

        @Override
        public String getSourcePath() {
            return getData().getParentPath();
        }

        @Override
        public String getDestinationPath() {
            return mDestination.getPath();
        }

        @Override
        public UpdatableData getUpdateData() {
            return null;
        }

        public void setDestination(String path) {
            mDestination = new FileItem(path);
        }

        @Override
        public void revert() {
        }
    }

}