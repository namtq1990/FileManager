package com.tqnam.filemanager.model;

import com.quangnam.baseframework.Log;
import com.quangnam.baseframework.TrackingTime;
import com.quangnam.baseframework.exception.SystemException;
import com.quangnam.baseframework.utils.RxCacheWithoutError;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.utils.FileUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by quangnam on 11/16/16.
 * Project FileManager-master
 */
public class CopyFileOperator extends Operator.TraverseFileOperator<FileItem> {
    public static final String TAG = CopyFileOperator.class.getCanonicalName();
    private static final int UPDATE_TIME = 800;

    private Observable<CopyFileData> mCurObservable;
    private CopyFileData mResult;
    private String mDestinationPath;
    private String mSourcePath;
    private long mLastEmitSize;

    public CopyFileOperator(List<FileItem> data, String destPath) {
        super(data);

        if (data == null || data.isEmpty()) {
            throw new SystemException("List data is empty. Operator is aborted");
        }

        mDestinationPath = destPath;
        mSourcePath = data.get(0).getParentPath();

        mResult = new CopyFileData();
        mResult.setOperatorHashcode(hashCode());

        traverse();
        Log.d("Data traversed: " + mResult);
    }

    @Override
    public Observable<CopyFileData> execute(Object... arg) {
        if (mCurObservable == null) {
            TrackingTime.beginTracking(formatTag(mResult));

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
                    .map(new Func1<Long, CopyFileData>() {
                        @Override
                        public CopyFileData call(Long aLong) {
                            long time = TrackingTime.endTracking(formatTag(mResult));
                            mResult.speed = (mResult.sizeCopied - mLastEmitSize) * 1000 / (float) (time == 0 ? UPDATE_TIME : time);
                            mLastEmitSize = mResult.sizeCopied;
                            mResult.validate();
                            TrackingTime.beginTracking(formatTag(mResult));

                            return mResult;
                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            TrackingTime.endTracking(formatTag(mResult));
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(new RxCacheWithoutError<CopyFileData>(1));
        }

        return mCurObservable;
    }

    private void execute() {
        try {
            Log.d("Copying...");
            ArrayList<Operator> list = getAllStream();

            for (Operator operator : list) {
                SingleFileCopyOperator copyOperator = (SingleFileCopyOperator) operator;
                copyOperator.execute(false);
            }

            mResult.setError(false);
        } catch (Throwable e) {
            e.printStackTrace();
            mResult.setError(true);

            throw e;
        }
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public boolean isAbleToPause() {
        return true;
    }

    private String formatTag(CopyFileData data) {
        return TAG + data.hashCode();
    }

//    private CopyFileData makeUpdatableData(CopyFileData data, int byteRead) {
//        long time = TrackingTime.endTracking(formatTag(data)) / 1000; // Format time in second
//        data.sizeTotal = mSizeTotal;
//        data.sizeCopied = mSizeCopied;
//        data.speed = (float) byteRead / time;
//        data.isFinished = false;
//
//        return data;
//    }

    @Override
    public String getSourcePath() {
        return mSourcePath;
    }

    @Override
    public String getDestinationPath() {
        return mDestinationPath;
    }

    @Override
    public UpdatableData getUpdateData() {
        return mResult;
    }

    @Override
    public Operator createStreamFromData(FileItem data) {
        if (!data.isDirectory()) {
            mResult.sizeTotal += data.length();
        }

        SingleFileCopyOperator operator = new SingleFileCopyOperator(data);
        operator.setDestination(data.getPath().replaceFirst(mSourcePath, mDestinationPath));

        return operator;
    }

    public static class CopyFileData extends UpdatableData {

        private float speed;
        private long sizeCopied;
        private long sizeTotal;
        private boolean isFinished;

        CopyFileData() {
            isFinished = false;
        }

        public float getSpeed() {
            return speed;
        }

        @Override
        public boolean isFinished() {
            return isFinished;
        }

        public long getSizeCopied() {
            return sizeCopied;
        }

        public long getSizeTotal() {
            return sizeTotal;
        }

        @Override
        public void validate() {
            setProgress((int)(sizeTotal == 0 ? 0 : (sizeCopied * 100 + 1) / sizeTotal));
        }

        @Override
        public String toString() {
            return super.toString() +
                    "{speed=" + speed + ", sizeCopied=" + sizeCopied + ", sizeTotal=" + sizeTotal + ", isFinish:" + isFinished + "}";
        }
    }

    public class SingleFileCopyOperator extends SingleFileOperator<FileItem> {

        private FileItem mDestination;

        public SingleFileCopyOperator(FileItem data) {
            super(data);

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
                    }
                }
            } else {
                FileInputStream inputStream = null;
                FileOutputStream outputStream = null;

                try {
                    inputStream = new FileInputStream(source);
                    outputStream = new FileOutputStream(mDestination, isOverwrite);
                    byte[] buff = new byte[FileUtil.BUFF_SIZE];
                    int byteRead;

                    while ((byteRead = inputStream.read(buff, 0, buff.length)) > 0) {
                        outputStream.write(buff);
                        mResult.sizeCopied += byteRead;
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
    }
}
