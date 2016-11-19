package com.tqnam.filemanager.utils;

import com.tqnam.filemanager.explorer.fileExplorer.FileItem;
import com.tqnam.filemanager.model.CopyFileOperator;
import com.tqnam.filemanager.model.Operator;

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

//    private ArrayList<Operator> mPrepareList;
    private static OperatorManager mInstance;

    private ArrayList<Operator> mCopyOperator;
    private ArrayList<Operator> mDeleteOperator;
    private ArrayList<Operator> mMoveOperator;
    private ArrayList<Operator> mOtherList;

    private OperatorManager() {
//        mPrepareList = new ArrayList<>();

        mCopyOperator = new ArrayList<>();
        mDeleteOperator = new ArrayList<>();
        mMoveOperator = new ArrayList<>();
        mOtherList = new ArrayList<>();
    }

    public static OperatorManager getInstance() {
        if (mInstance == null)
            mInstance = new OperatorManager();

        return mInstance;
    }

    public static CopyFileOperator makeCopy(List<FileItem> data, String path) {
        return new CopyFileOperator(data, path);
    }

    public ArrayList<Operator> getOperatorList(int category) {
        switch (category) {
            case CATEGORY_COPY:
                return mCopyOperator;
            case CATEGORY_DELETE:
                return mDeleteOperator;
            case CATEGORY_MOVE:
                return mMoveOperator;
            default:
                return mOtherList;
        }
    }

    /**
     * Add operation to Manager and will be started
     *
     */
    public void addOperator(Operator<?> operator, int category) {
//        mPrepareList.remove(operator);

        ArrayList<Operator> list = getOperatorList(category);
        list.add(operator);

        Observable<?> observable = operator.execute();
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

//    public void addPrepareList(Operator operator) {
//        mPrepareList.add(operator);
//    }
}
