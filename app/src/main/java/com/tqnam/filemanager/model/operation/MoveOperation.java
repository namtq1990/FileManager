package com.tqnam.filemanager.model.operation;

import com.quangnam.baseframework.Log;
import com.quangnam.baseframework.TrackingTime;
import com.quangnam.baseframework.exception.SystemException;
import com.quangnam.baseframework.utils.RxCacheWithoutError;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.ErrorCode;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.operation.propertyView.BasicOperationPropertyView;

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
 * Created by quangnam on 11/24/16.
 * Project FileManager-master
 */
public class MoveOperation extends BasicOperation<FileItem> implements Validator.ValidateAction {
    private static final String TAG = MoveOperation.class.getSimpleName();

    private HashMap<String, String> mDestinationPathStorage;
    private String mSourcePath;
    private String mDestinationPath;


    public MoveOperation(List<FileItem> data, String destPath) {
        super(data);

        mDestinationPathStorage = new HashMap<>();
        mDestinationPath = destPath;
        mSourcePath = data.get(0).getParentPath();
        super.mPropertyView = new BasicOperationPropertyView(this);

        mResult = new BasicUpdatableData();
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
    public Operation createStreamFromData(FileItem data) {
        if (!data.isDirectory()) {
            mResult.setSizeTotal(mResult.getSizeTotal() + data.length());
        }

        SingleMoveFile operation = new SingleMoveFile(data);
        operation.setDestinationPath(data.getPath().replaceFirst(mSourcePath, mDestinationPath));

        return operation;
    }

    private String formatTag(BasicOperation.BasicUpdatableData data) {
        return TAG + data.hashCode();
    }

    @Override
    public Observable<? extends BasicUpdatableData> createExecuter() {
        TrackingTime.beginTracking(formatTag(mResult));

        if (isOverwrite()) {
            if (getSourcePath().equals(getDestinationPath())) {
                mResult.setProgress(100);
                mCurObservable = Observable.just(mResult);
            } else {
                CopyFileOperation copyOperation = new CopyFileOperation(getData(), getDestinationPath());
                DeleteOperation deleteOperation = new DeleteOperation(getData());
                copyOperation.setOverwrite(true);
                copyOperation.getValidator().clear();

                mCurObservable = copyOperation.execute()
                        .map(new Func1<BasicUpdatableData, BasicUpdatableData>() {
                            @Override
                            public BasicUpdatableData call(BasicUpdatableData copyFileData) {
                                int progress = copyFileData.getProgress();
                                if (progress > 99)
                                    progress = 99;

                                mResult.setSizeExecuted(copyFileData.getSizeExecuted());
                                mResult.setSizeTotal(copyFileData.getSizeTotal());
                                mResult.setProgress(progress);
                                mResult.setSpeed(copyFileData.getSpeed());
                                return mResult;
                            }
                        })
                        .concatWith(
                                deleteOperation.execute()
                                        .map(new Func1<BasicUpdatableData, BasicUpdatableData>() {
                                            @Override
                                            public BasicUpdatableData call(DeleteOperation.BasicUpdatableData deleteFileData) {
                                                if (deleteFileData.isFinished()) {
                                                    mResult.setProgress(100);
                                                    mResult.setFinished(true);
                                                }

                                                mResult.setError(deleteFileData.isError());

                                                return mResult;
                                            }
                                        })
                        );
            }
        } else {
            for (FileItem item : getData()) {
                Operation operation = createStreamFromData(item);
                getAllStream().add(operation);
            }

            mCurObservable = Observable.interval(UPDATE_TIME, TimeUnit.MILLISECONDS).takeUntil(
                    Observable.create(new Observable.OnSubscribe<Long>() {
                        @Override
                        public void call(Subscriber<? super Long> subscriber) {
                            execute();
                            subscriber.onCompleted();
                        }
                    })
                            .subscribeOn(Schedulers.io()))
                    .concatWith(Observable.just(0L))
                    .map(new Func1<Long, BasicUpdatableData>() {
                        @Override
                        public BasicUpdatableData call(Long aLong) {
                            long time = TrackingTime.endTracking(formatTag(mResult));
                            mResult.setSpeed(mResult.getSizeExecuted() - (getLastEmitSize()) * 1000 / (float) (time == 0 ? UPDATE_TIME : time));
                            setLastEmitSize(mResult.getSizeExecuted());
                            TrackingTime.beginTracking(formatTag(mResult));
                            Log.d("Mapping data: " + mResult);
                            mResult.validate();
                            return mResult;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread());
        }

        mCurObservable = mCurObservable
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mResult.setError(true);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        TrackingTime.endTracking(formatTag(mResult));
                        mResult.setProgress(100);
                        mResult.setError(false);
                    }
                })
                .compose(new RxCacheWithoutError<BasicUpdatableData>(1));

        return mCurObservable;
    }

    private void execute() {
        try {
            Log.d("Moving...");
            ArrayList<Operation> operations = getAllStream();

            for (int i = 0; i < operations.size(); i++) {
                SingleMoveFile moveOperator = (SingleMoveFile) operations.get(i);
                moveOperator.execute(false);
            }

            mResult.setError(false);
        } catch (Throwable e) {
            e.printStackTrace();
            mResult.setError(true);

            throw e;
        }
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
    public boolean isUndoable() {
        return false;
    }

    @Override
    public boolean isAbleToPause() {
        return true;
    }

    @Override
    public int validate(ItemExplorer item) {
        int mode = 0;
        String destinationPath = mDestinationPathStorage.get(item.getPath());

        if (destinationPath != null) {
            FileItem destFile = new FileItem(destinationPath);
            if (destFile.exists()) {
                mode = getValidator().setModeViolated(Validator.MODE_FILE_EXIST, mode, true);
            }
            if ((destFile.exists() && !destFile.canWrite())
                    || (!destFile.exists() && !destFile.getParentItem().canWrite())) {
                mode = getValidator().setModeViolated(Validator.MODE_PERMISSION, mode, true);
            }
        }

        return mode;
    }

    @Override
    public void setItemValidated(ItemExplorer item) {
        super.setItemValidated(item);
        getData().remove(item);
    }

    public class SingleMoveFile extends SingleFileOperation<FileItem> {
        FileItem mDestination;

        public SingleMoveFile(FileItem data) {
            super(data);
        }

        @Override
        public Observable<?> execute(Object... arg) {
            return null;
        }

        private void execute(boolean isOverwrite) {
            if (mDestination.exists() && !isOverwrite) {
                throw new SystemException(ErrorCode.RK_EXPLORER_FILE_EXISTED,
                        "Can't move file because file " + mDestination + " existed");
            }
            FileItem data = getData();
            long dataLength = data.length();

            Log.d("Moving " + data.getPath());

            boolean isMoved = data.renameTo(mDestination);
            if (!isMoved) {
                if (!mDestination.getParentFile().canWrite()) {
                    throw new SystemException(ErrorCode.RK_EXPLORER_PERMISSION, "Can't move because permission don't allow");
                }

                throw new SystemException(ErrorCode.RK_MOVE_ERR, "Can't move file " + data.getPath());
            }

            if (!data.isDirectory())
                mResult.setSizeExecuted(mResult.getSizeExecuted() + dataLength);
        }

        @Override
        public String getSourcePath() {
            return getData().getParentPath();
        }

        @Override
        public String getDestinationPath() {
            return mDestination.getPath();
        }

        public void setDestinationPath(String path) {
            mDestination = new FileItem(path);
        }

        @Override
        public UpdatableData getUpdateData() {
            return null;
        }
    }

}
