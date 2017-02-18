/*
 * MIT License
 *
 * Copyright (c) 2017 Tran Quang Nam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import com.tqnam.filemanager.utils.OperationManager;

import org.greenrobot.eventbus.EventBus;

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

    private CopyFileOperation mCopyOperation;
    private DeleteOperation mDeleteOperation;

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
    public int getCategory() {
        return OperationManager.CATEGORY_MOVE;
    }

    @Override
    public void cancel() {
        super.cancel();

        if (mCopyOperation != null) {
            mCopyOperation.cancel();
        }
        if (mDeleteOperation != null) {
            mDeleteOperation.cancel();
        }
    }

    @Override
    public Observable<? extends BasicUpdatableData> createExecuter() {
        TrackingTime.beginTracking(formatTag(mResult));
        //TODO: Separate between use move function or copy/delete function isn't exactly

        if (isOverwrite()) {
            if (getSourcePath().equals(getDestinationPath())) {
                mResult.setProgress(100);
                mCurObservable = Observable.just(mResult);
            } else {
                mCopyOperation = new CopyFileOperation(getData(), getDestinationPath());
                mDeleteOperation = new DeleteOperation(getData());
                mCopyOperation.setOverwrite(true);
                mDeleteOperation.getValidator().clear();

                mCurObservable = mCopyOperation.execute()
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
                                mDeleteOperation.execute()
                                        .map(new Func1<BasicUpdatableData, BasicUpdatableData>() {
                                            @Override
                                            public BasicUpdatableData call(DeleteOperation.BasicUpdatableData deleteFileData) {
                                                if (deleteFileData.isFinished()) {
                                                    mResult.setProgress(100);
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
                });

        return mCurObservable;
    }

    private void execute() {
        try {
            Log.d("Moving...");
            ArrayList<Operation> operations = getAllStream();

            for (int i = 0; i < operations.size(); i++) {
                if (isCancelled()) {
                    break;
                }

                performLockIfOperationPaused();

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
    public void revert() {
        //TODO revert function isn't completed. Don't check if single operation is completed. So need separate all single operation to standalone
        super.revert();

        ArrayList<FileItem> moveFileData = new ArrayList<>();
        for (FileItem file : getData()) {
            FileItem inputFile = new FileItem(file.getPath().replaceFirst(getSourcePath(), getDestinationPath()));
            if (inputFile.exists()) {
                moveFileData.add(inputFile);
            }
        }

        MoveOperation moveOperation = new MoveOperation(moveFileData, getSourcePath());
        moveOperation.execute(null)
                .subscribe(new Action1<BasicUpdatableData>() {
                    @Override
                    public void call(BasicUpdatableData basicUpdatableData) {
                        EventBus.getDefault().post(new LocalRefreshDataEvent());
                    }
                }, new DefaultErrorAction());
    }

    @Override
    public void setRunning(boolean running) {
        super.setRunning(running);

        if (mCopyOperation != null) {
            mCopyOperation.setRunning(running);
        }

        if (mDeleteOperation != null) {
            mDeleteOperation.setRunning(running);
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
    public int validate(ItemExplorer item) {
        int mode = 0;
        String destinationPath = mDestinationPathStorage.get(item.getPath());

        if (destinationPath != null) {
            FileItem destFile = new FileItem(destinationPath);
            if (destFile.getPath().equals(item.getPath())) {
                mode = getValidator().setModeViolated(Validator.MODE_SAME_FILE, mode, true);
            }
            else if (destFile.exists()) {
                mode = getValidator().setModeViolated(Validator.MODE_FILE_EXIST, mode, true);
            }
            if ((destFile.exists() && !destFile.canWrite())
                    || (!destFile.exists() && !destFile.getParentItem().canWrite())) {
                mode = getValidator().setModeViolated(Validator.MODE_PERMISSION, mode, true);
            }
        }

        return mode;
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

            if (isCancelled()) {
                return;
            }

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
