package com.tqnam.filemanager.model;

import com.quangnam.baseframework.Log;
import com.quangnam.baseframework.exception.SystemException;
import com.tqnam.filemanager.explorer.fileExplorer.FileItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by quangnam on 11/18/16.
 * Project FileManager-master
 */
public class DeleteOperator extends Operator.TraverseFileOperator<FileItem> {
    private static final int UPDATE_TIMESTAMP = 100;

    private DeleteFileData mResult;

    public DeleteOperator(List<FileItem> data) {
        super(data);

        mResult = new DeleteFileData();
        mResult.numOfFile = getAllStream().size();
        mResult.setOperatorHashcode(hashCode());

        traverse();
    }

    @Override
    public Operator createStreamFromData(FileItem data) {
        mResult.numOfFile++;
        return new SingleDeleteFile(data);
    }

    @Override
    public Observable<DeleteFileData> execute(Object... arg) {
        return Observable.interval(UPDATE_TIMESTAMP, TimeUnit.MILLISECONDS).takeUntil(
                Observable.create(new Observable.OnSubscribe<Long>() {
                    @Override
                    public void call(Subscriber<? super Long> subscriber) {
                        execute();
                        subscriber.onCompleted();
                    }
                })
                        .subscribeOn(Schedulers.io()))
                .concatWith(Observable.just(0L))
                .map(new Func1<Long, DeleteFileData>() {
                    @Override
                    public DeleteFileData call(Long aLong) {
                        Log.d("Mapping data: " + mResult);
                        mResult.validate();
                        return mResult;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void execute() {
        Log.d("Executing...");
        ArrayList<Operator> operators = getAllStream();

        for (int i = operators.size() - 1; i >= 0; i--) {
            SingleDeleteFile deleteOperator = (SingleDeleteFile) operators.get(i);
            deleteOperator.execute();
            mResult.numOfFileDeleted++;
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
    public boolean isUpdatable() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public boolean isAbleToPause() {
        return true;
    }

    public static class SingleDeleteFile extends SingleFileOperator<FileItem> {

        public SingleDeleteFile(FileItem data) {
            super(data);
        }

        @Override
        public Observable<?> execute(Object... arg) {
            return null;
        }

        private void execute() {
            FileItem data = getData();

            Log.d("Deleting " + data.getPath());

            boolean isDeleted = data.delete();
            if (!isDeleted) {
                throw new SystemException("Can't delete file " + data.getPath());
            }
        }

        @Override
        public String getSourcePath() {
            return getData().getParentPath();
        }

        @Override
        public String getDestinationPath() {
            return null;
        }
    }

    public static class DeleteFileData extends UpdatableData {
        private int numOfFile;
        private int numOfFileDeleted;

        @Override
        public void validate() {
            if (numOfFile != 0)
                setProgress((numOfFileDeleted * 100 + 1) / numOfFile);
        }

        @Override
        public String toString() {
            return super.toString()
                    + "{numOfFile=" + numOfFile
                    + ", numOfFileDeleted=" + numOfFileDeleted + "}";
        }
    }
}
