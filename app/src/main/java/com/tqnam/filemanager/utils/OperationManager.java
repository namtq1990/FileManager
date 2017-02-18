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
public class OperationManager {
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
    private static OperationManager mInstance;

    private ArrayList<Operation> mCopyOperation;
    private ArrayList<Operation> mDeleteOperation;
    private ArrayList<Operation> mMoveOperation;
    private ArrayList<Operation> mOtherList;

    private OperationManager() {
//        mPrepareList = new ArrayList<>();

        mCopyOperation = new ArrayList<>();
        mDeleteOperation = new ArrayList<>();
        mMoveOperation = new ArrayList<>();
        mOtherList = new ArrayList<>();
    }

    public static OperationManager getInstance() {
        if (mInstance == null)
            mInstance = new OperationManager();

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
