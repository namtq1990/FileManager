package com.tqnam.filemanager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Observable;

public abstract class Operator<T> {

    private T mData;

    public Operator(T data) {
        mData = data;
    }

    public abstract Observable<?> execute(Object... arg);

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
        return getUpdateData() != null;
    }

    public abstract String getSourcePath();

    public abstract String getDestinationPath();

    public T getData() {
        return mData;
    }

    public abstract UpdatableData getUpdateData();

    public abstract static class TraverseFileOperator<T extends ItemExplorer> extends Operator<List<T>> {

        private ArrayList<Operator> mStreamList;
        private ArrayList<Operator> mExecutingList;
        private ArrayList<Operator> mExecutedList;

        public TraverseFileOperator(List<T> data) {
            super(data);

            mStreamList = new ArrayList<>(10);
            mExecutingList = new ArrayList<>(10);
            mExecutedList = new ArrayList<>(10);
        }

        public ArrayList<Operator> getAllStream() {
            return mStreamList;
        }

        public ArrayList<Operator> getExecutingList() {
            return mExecutingList;
        }

        public ArrayList<Operator> getExecutedList() {
            return mExecutedList;
        }

        public final void traverse() {
            List<T> data = new ArrayList<>(getData());

            while (!data.isEmpty()) {
                T file = data.remove(0);

                Operator operator = createStreamFromData(file);
                mStreamList.add(operator);

                if (file.isDirectory()) {
                    data.addAll((Collection<? extends T>) file.getChild());
                }
            }
        }

        protected void moveToExecuting(Operator operator) {
            if (!mExecutingList.contains(operator))
                mExecutingList.add(operator);
        }

        protected void moveToExecuted(Operator operator) {
            mExecutingList.remove(operator);
            mExecutedList.add(operator);
        }

        public abstract Operator createStreamFromData(T data);
    }

    public abstract static class SingleFileOperator<T extends ItemExplorer> extends Operator<T> {

        public SingleFileOperator(T data) {
            super(data);
        }
    }

    public static class UpdatableData {
        private int progress;
        private int operatorHashcode;
        private boolean isError;

        public boolean isFinished() {
            return progress >= 100;
        }

        public float getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public long getOperatorHashcode() {
            return operatorHashcode;
        }

        public void setOperatorHashcode(int hashcode) {
            operatorHashcode = hashcode;
        }

        public boolean isError() {
            return isError;
        }

        public void setError(boolean error) {
            isError = error;
        }

        public void validate() {}

        @Override
        public String toString() {
            return super.toString()
                    + "{progress=" + progress + "}";
        }
    }

}
