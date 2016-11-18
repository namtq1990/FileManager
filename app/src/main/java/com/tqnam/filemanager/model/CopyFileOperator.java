package com.tqnam.filemanager.model;

import com.quangnam.baseframework.Log;
import com.quangnam.baseframework.TrackingTime;
import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.utils.FileUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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

//    private Func1<Throwable, Observable<?>> mRetryWhenError;
    private long mSizeOfListExecuted;
    private CopyFileData mResult;
    private String mDestinationPath;
    private String mSourcePath;
    private Func1<Throwable, Observable<?>> mRetryWhenError;

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
        TrackingTime.beginTracking(formatTag(mResult));

        ArrayList<Observable<CopyFileData>> list = new ArrayList<>(getAllStream().size());
        for (final Operator<Object> operator : getAllStream()) {
            Observable<CopyFileData> childObservable = operator.execute()
                    .map(new Func1<Object, CopyFileData>() {
                        @Override
                        public CopyFileData call(Object o) {
                            if (o instanceof CopyFileData)
                                return (CopyFileData) o;

                            return null;
                        }
                    }).doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            moveToExecuting(operator);
                        }
                    }).doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            moveToExecuted(operator);
                        }
                    });
            list.add(childObservable);
        }

        return Observable.concat(Observable.from(list))
                .map(new Func1<CopyFileData, CopyFileData>() {
                    @Override
                    public CopyFileData call(CopyFileData data) {
                        long timeExecuted = TrackingTime.endTracking(formatTag(mResult));
                        long oldSizeCopied = mResult.sizeCopied;

                        mResult.sizeCopied = mSizeOfListExecuted + data.sizeCopied;

                        mResult.speed = (mResult.sizeCopied - oldSizeCopied) * 1000 / (float)timeExecuted;
                        mResult.setProgress((int)((mResult.sizeCopied * 100 + 1) / mResult.sizeTotal));

                        TrackingTime.beginTracking(formatTag(mResult));

                        return mResult;
                    }
                });
    }

    private void moveToExecuting(Operator operator) {
        ArrayList<Operator> executingList = getExecutingList();
        if (!executingList.contains(operator)) executingList.add(operator);
    }

    private void moveToExecuted(Operator operator) {
        if (!(operator instanceof SingleFileCopyOperator)) {
            throw new SystemException(ErrorCode.RK_COPY_ERR, "Cann't use operator that's not " + SingleFileCopyOperator.class);
        }

        ArrayList<Operator> executingList = getExecutingList();
        ArrayList<Operator> executedList = getExecutedList();
        executingList.remove(operator);
        if (!executedList.contains(operator)) {
            mSizeOfListExecuted += ((SingleFileCopyOperator) operator).operatorResult.sizeTotal;
            executedList.add(operator);
        }
    }

    @Override
    public boolean isUpdatable() {
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

    public void setRetryFlatMap(Func1<Throwable, Observable<?>> flatMapFunc) {
        mRetryWhenError = flatMapFunc;
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
        public String toString() {
            return super.toString() +
                    "{speed=" + speed + ", sizeCopied=" + sizeCopied + ", sizeTotal=" + sizeTotal + ", isFinish:" + isFinished + "}";
        }
    }

    public class SingleFileCopyOperator extends SingleFileOperator<FileItem> {

        private CopyFileData operatorResult;
        private FileItem mDestination;

        public SingleFileCopyOperator(FileItem data) {
            super(data);

            Log.d("make operation for item: " + data.getAbsolutePath());

            operatorResult = new CopyFileData();
            operatorResult.sizeTotal = data.isDirectory() ? 0 : data.length();
        }

        @Override
        public Observable execute(Object... arg) {
            Observable<CopyFileData> observable;
            if (getData().isDirectory()) {
                observable = Observable.fromCallable(new Callable<CopyFileData>() {
                    @Override
                    public CopyFileData call() throws Exception {
                        boolean isMakeDir = mDestination.mkdir();
                        if (!isMakeDir) {
                            throw new SystemException(ErrorCode.RK_COPY_ERR,
                                    "Cann't make directory " + mDestination.getPath(),
                                    new IOException());
                        }

                        operatorResult.isFinished = true;
                        Log.d("Emitting Folder: " + operatorResult + ", cur data: " + mResult);

                        return operatorResult;
                    }
                });
            } else {
                return Observable.create(new Observable.OnSubscribe<CopyFileData>() {

                    @Override
                    public void call(Subscriber<? super CopyFileData> subscriber) {
                        if (getDestinationPath() == null) {
                            throw new SystemException(ErrorCode.RK_COPY_ERR, "Hasn't set destination path");
                        }
                        FileInputStream inputStream = null;
                        FileOutputStream outputStream = null;

                        try {
                            inputStream = new FileInputStream(getData());
                            outputStream = new FileOutputStream(mDestination);
                            byte[] buff = new byte[FileUtil.BUFF_SIZE];
                            int byteRead;
                            long oldCopiedSent = 0;

                            while ((byteRead = inputStream.read(buff, 0, buff.length)) > 0) {
                                //                            TrackingTime.beginTracking(formatTag(result));
                                outputStream.write(buff);
                                makeUpdatableData(operatorResult, byteRead);
                                if (operatorResult.sizeCopied - oldCopiedSent > 500000) {
                                    Log.d("Emitting File: " + operatorResult + ", curData: " + mResult);
                                    subscriber.onNext(operatorResult);
                                    oldCopiedSent = operatorResult.sizeCopied;
                                }
                            }

                            operatorResult.isFinished = true;
                            Log.d("COmpleted " + operatorResult);
                            subscriber.onCompleted();
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
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onBackpressureLatest();
            }

            return observable
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
                    .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                        @Override
                        public Observable<?> call(Observable<? extends Throwable> error) {
                            return error.flatMap(mRetryWhenError);
                        }
                    });
        }

        private CopyFileData makeUpdatableData(CopyFileData data, int byteRead) {
//            long time = TrackingTime.endTracking(formatTag(data)) / 1000; // Format time in second
//            data.sizeTotal = mTotalSize;
            data.sizeCopied += byteRead;
//            data.speed = (float) byteRead / time;
            data.isFinished = false;

            return data;
        }

        @Override
        public String getSourcePath() {
            return getData().getParentPath();
        }

        @Override
        public String getDestinationPath() {
            return mDestination.getPath();
        }

        public void setDestination(String path) {
            mDestination = new FileItem(path);
        }
    }
}
