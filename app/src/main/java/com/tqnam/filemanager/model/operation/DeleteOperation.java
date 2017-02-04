package com.tqnam.filemanager.model.operation;

import com.quangnam.baseframework.Log;
import com.quangnam.baseframework.TrackingTime;
import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.operation.propertyView.BasicOperationPropertyView;
import com.tqnam.filemanager.utils.OperationManager;

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
 * Created by quangnam on 11/18/16.
 * Project FileManager-master
 */
public class DeleteOperation extends BasicOperation<FileItem> {
    private static final String TAG = DeleteOperation.class.getSimpleName();

    private BasicOperation.BasicUpdatableData mResult;

    public DeleteOperation(List<FileItem> data) {
        super(data);

        mResult = new BasicOperation.BasicUpdatableData();
        mResult.setOperatorHashcode(hashCode());
        super.mPropertyView = new BasicOperationPropertyView(this);

        traverse();
    }

    @Override
    public Operation createStreamFromData(FileItem data) {
        if (!data.isDirectory()) {
            mResult.setSizeTotal(mResult.getSizeTotal() + data.length());
        }

        return new SingleDeleteFile(data);
    }

    private String formatTag(BasicOperation.BasicUpdatableData data) {
        return TAG + data.hashCode();
    }

    @Override
    public int getCategory() {
        return OperationManager.CATEGORY_DELETE;
    }

    @Override
    public Observable<? extends BasicUpdatableData> createExecuter() {
        TrackingTime.beginTracking(formatTag(mResult));
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
                .map(new Func1<Long, BasicOperation.BasicUpdatableData>() {
                    @Override
                    public BasicOperation.BasicUpdatableData call(Long aLong) {
                        long time = TrackingTime.endTracking(formatTag(mResult));
                        mResult.setSpeed(mResult.getSizeExecuted() - (getLastEmitSize()) * 1000 / (float) (time == 0 ? UPDATE_TIME : time));
                        setLastEmitSize(mResult.getSizeExecuted());
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
                .observeOn(AndroidSchedulers.mainThread());

        return mCurObservable;
    }

    private void execute() {
        try {
            Log.d("Deleting...");
            ArrayList<Operation> operations = getAllStream();

            for (int i = operations.size() - 1; i >= 0; i--) {
                if (isCancelled()) {
                    break;
                }

                SingleDeleteFile deleteOperator = (SingleDeleteFile) operations.get(i);
                deleteOperator.execute();
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
        return getData().get(0).getParentPath();
    }

    @Override
    public String getDestinationPath() {
        return null;
    }

    @Override
    public UpdatableData getUpdateData() {
        return mResult;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    public class SingleDeleteFile extends SingleFileOperation<FileItem> {

        public SingleDeleteFile(FileItem data) {
            super(data);
        }

        @Override
        public Observable<?> execute(Object... arg) {
            return null;
        }

        private void execute() {
            FileItem data = getData();
            long length = data.length();

            if (isCancelled()) {
                return;
            }

            performLockIfOperationPaused();
            Log.d("Deleting " + data.getPath());

            boolean isDeleted = data.delete();
            if (!isDeleted) {
                throw new SystemException("Can't delete file " + data.getPath());
            }

            if (!data.isDirectory())
                mResult.setSizeExecuted(mResult.getSizeExecuted() + length);
        }

        @Override
        public String getSourcePath() {
            return getData().getParentPath();
        }

        @Override
        public String getDestinationPath() {
            return null;
        }

        @Override
        public UpdatableData getUpdateData() {
            return null;
        }
    }
}
