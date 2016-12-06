package com.tqnam.filemanager.utils;

import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.operation.CopyFileOperation;
import com.tqnam.filemanager.model.operation.Operation;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by quangnam on 11/17/16.
 * Project FileManager-master
 */
public class OperatorManager {
    public static final int CATEGORY_COPY = 0;
    public static final int CATEGORY_MOVE = 1;
    public static final int CATEGORY_DELETE = 2;
    public static final int CATEGORY_OTHER = 10;

    public static final int[] CATEGORIES = {
            CATEGORY_COPY,
            CATEGORY_MOVE,
            CATEGORY_DELETE,
            CATEGORY_OTHER
    };

//    private ArrayList<Operation> mPrepareList;
    private static OperatorManager mInstance;

    private ArrayList<Operation> mCopyOperation;
    private ArrayList<Operation> mDeleteOperation;
    private ArrayList<Operation> mMoveOperation;
    private ArrayList<Operation> mOtherList;

    private OperatorManager() {
//        mPrepareList = new ArrayList<>();

        mCopyOperation = new ArrayList<>();
        mDeleteOperation = new ArrayList<>();
        mMoveOperation = new ArrayList<>();
        mOtherList = new ArrayList<>();
    }

    public static OperatorManager getInstance() {
        if (mInstance == null)
            mInstance = new OperatorManager();

        return mInstance;
    }

    public static CopyFileOperation makeCopy(List<FileItem> data, String path) {
        return new CopyFileOperation(data, path);
    }

    public ArrayList<Operation> getOperatorList(int category) {
        switch (category) {
            case CATEGORY_COPY:
                return mCopyOperation;
            case CATEGORY_DELETE:
                return mDeleteOperation;
            case CATEGORY_MOVE:
                return mMoveOperation;
            default:
                return mOtherList;
        }
    }

    /**
     * Add operation to Manager and will be started
     *
     */
    public void addOperator(Operation<?> operation, int category) {
//        mPrepareList.remove(operation);

        ArrayList<Operation> list = getOperatorList(category);
        list.add(operation);

        Observable<?> observable = operation.execute();
        observable
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        // So don't handle here
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

//    public void addPrepareList(Operation operator) {
//        mPrepareList.add(operator);
//    }
}
