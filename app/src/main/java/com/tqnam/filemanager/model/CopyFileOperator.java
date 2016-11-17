package com.tqnam.filemanager.model;

import com.quangnam.baseframework.Log;
import com.quangnam.baseframework.TrackingTime;
import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.utils.FileUtil;
import com.tqnam.filemanager.utils.OperatorBuilder;

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
public class CopyFileOperator extends Operator.MultipleItemOperator<FileItem> {
    public static final String TAG = CopyFileOperator.class.getCanonicalName();

    private List<SingleFileCopyOperator> mQueueToExecute;
    private List<SingleFileCopyOperator> mListExecuting;
    private List<SingleFileCopyOperator> mListExecuted;

//    private Func1<Throwable, Observable<?>> mRetryWhenError;
    private long mSizeOfListExecuted;
    private CopyFileData mResult;
    private String mDestinationPath;

    public CopyFileOperator(List<FileItem> data, String destPath) {
        super(data);

        if (data == null || data.isEmpty()) {
            throw new SystemException("List data is empty. Operator is aborted");
        }

//        FileItem parent = new FileItem(data.get(0).getParent());
        mDestinationPath = destPath;

        mQueueToExecute = new ArrayList<>(data.size());
        mListExecuted = new ArrayList<>(data.size());
        mListExecuting = new ArrayList<>(10);

        mResult = new CopyFileData();
        mResult.setOperatorHashcode(hashCode());

        // Traverse over list file tree to create data for mQueueToExecute, mListExecuted and mListExecuting
        for (FileItem file : data) {
            SingleFileCopyOperator operator = new SingleFileCopyOperator(file);
            operator.setDestination(destPath + "/" + file.getDisplayName());
            mQueueToExecute.add(operator);

            mResult.sizeTotal += file.getSize();
        }
    }

    @Override
    public Observable<CopyFileData> execute(Object... arg) {
        TrackingTime.beginTracking(formatTag(mResult));
//        ArrayList<Observable<CopyFileData>> listObservables = new ArrayList<>(mFileToExecute.size());
        Observable<Observable<CopyFileData>> observable = Observable.create(new Observable.OnSubscribe<Observable<CopyFileData>>() {
            @Override
            public void call(Subscriber<? super Observable<CopyFileData>> subscriber) {
                ArrayList<SingleFileCopyOperator> queue = new ArrayList<>(mQueueToExecute);
                Log.d(TAG, "Start making singleOperator " + queue);
                for (final SingleFileCopyOperator operator : queue) {
//                    final SingleFileCopyOperator operator = mQueueToExecute.get(i);
                    Observable<CopyFileData> execute = operator.execute()
                            .map(new Func1<Object, CopyFileData>() {

                                @Override
                                public CopyFileData call(Object o) {
                                    if (o instanceof UpdatableData)
                                        return (CopyFileData) o;

                                    Log.d(TAG, "Cann't parse object " + o);

                                    return null;
                                }
                            });

                    Log.d("Emitting observable");
                    subscriber.onNext(execute
                            .doOnSubscribe(new Action0() {
                                @Override
                                public void call() {
                                    moveFromQueueToExecute(operator);
                                }
                            })
                            .doOnCompleted(new Action0() {
                                @Override
                                public void call() {
                                    executedOperator(operator);
                                }
                            }));
                }

                subscriber.onCompleted();
            }
        });
        return Observable.concat(observable)
                .map(new Func1<CopyFileData, CopyFileData>() {
                    @Override
                    public CopyFileData call(CopyFileData data) {
                        long timeExecuted = TrackingTime.endTracking(formatTag(mResult));
                        long oldSizeCopied = mResult.sizeCopied;

                        mResult.sizeCopied = mSizeOfListExecuted + data.sizeCopied;
//                        mResult.sizeCopied += data.sizeCopied;

                        mResult.speed = (mResult.sizeCopied - oldSizeCopied) * 1000 / (float)timeExecuted;
                        mResult.setProgress((float) (mResult.sizeCopied * 100 / (double)mResult.sizeTotal));

                        TrackingTime.beginTracking(formatTag(mResult));

                        return mResult;
                    }
                });
    }

    private void moveFromQueueToExecute(SingleFileCopyOperator operator) {
        mQueueToExecute.remove(operator);
        if (!mListExecuting.contains(operator)) mListExecuting.add(operator);
    }

    private void executedOperator(SingleFileCopyOperator operator) {
        mListExecuting.remove(operator);
        mListExecuted.add(operator);

        mSizeOfListExecuted += operator.mResult.sizeTotal;
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
        return getData().get(0).getPath();
    }

    @Override
    public String getDestinationPath() {
        return mDestinationPath;
    }

    public void setRetryFlatMap(Func1<Throwable, Observable<?>> flatMapFunc) {
        for (SingleFileCopyOperator operator : mQueueToExecute) {
            operator.setRetryFunction(flatMapFunc);
        }
        for (SingleFileCopyOperator operator : mListExecuting) {
            operator.setRetryFunction(flatMapFunc);
        }
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

    public static class SingleFileCopyOperator extends SingleItemOperator<FileItem> {

        CopyFileData mResult;
        private FileItem mDestination;
        private Func1<Throwable, Observable<?>> mRetryWhenError;

        public SingleFileCopyOperator(FileItem data) {
            super(data);

            Log.d("make operation for item: " + data.getAbsolutePath());

            mResult = new CopyFileData();
            mResult.sizeTotal = data.getSize();
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

                        mResult.isFinished = true;
                        Log.d("Emitting " + mResult);

                        return mResult;
                    }
                }).flatMap(new Func1<CopyFileData, Observable<CopyFileData>>() {
                    @Override
                    public Observable<CopyFileData> call(CopyFileData data) {
                        if (data.isFinished) {
                            CopyFileOperator operator = OperatorBuilder.makeCopy(getData().getChild(),
                                    mDestination.getPath());
                            operator.setRetryFlatMap(mRetryWhenError);

                            return operator.execute();
                        }
                        return null;
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
                                makeUpdatableData(mResult, byteRead);
                                if (mResult.sizeCopied - oldCopiedSent > 500000) {
                                    Log.d("Emitting " + mResult);
                                    subscriber.onNext(mResult);
                                    oldCopiedSent = mResult.sizeCopied;
                                }
                            }

                            mResult.isFinished = true;
//                            subscriber.onNext(mResult);
                            Log.d("COmpleted " + mResult);
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
                }).onBackpressureLatest();
            }

            return observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
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
            return getData().getPath();
        }

        @Override
        public String getDestinationPath() {
            return mDestination.getPath();
        }

        public void setDestination(String path) {
            mDestination = new FileItem(path);
        }

        public void setRetryFunction(Func1<Throwable, Observable<?>> function) {
            mRetryWhenError = function;
        }
    }
}
