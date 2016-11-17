package com.tqnam.filemanager.model;

import java.util.List;

import rx.Observable;

/**
 * Created by quangnam on 11/14/16.
 */
public abstract class Operator<T> {

    private T mData;

    public abstract Observable execute(Object... arg);

    public boolean isExecuting() {
        return false;
    }

    public boolean isCancelable() {
        return false;
    }

    public boolean isUndoable() {
        return false;
    }

    public boolean isAbleToPause() {
        return false;
    }

    public boolean isUpdatable() {
        return false;
    }

    public abstract String getSourcePath();

    public abstract String getDestinationPath();

    public abstract T getData();

    public abstract static class SingleItemOperator<T> extends Operator {
        private T mData;

        public SingleItemOperator(T data) {
            mData = data;
        }

        public T getData() {
            return mData;
        }
    }

    public abstract static class MultipleItemOperator<T> extends Operator {
        private List<T> mData;

        public MultipleItemOperator(List<T> data) {
            mData = data;
        }

        public List<T> getData() {
            return mData;
        }
    }

    public static class UpdatableData {
        private float progress;
        private int operatorHashcode;

        public boolean isFinished() {
            return progress == 100;
        }

        public float getProgress() {
            return progress;
        }

        public void setProgress(float progress) {
            this.progress = progress;
        }

        public long getOperatorHashcode() {
            return operatorHashcode;
        }

        public void setOperatorHashcode(int hashcode) {
            operatorHashcode = hashcode;
        }
    }

}
