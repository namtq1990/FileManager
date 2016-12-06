package com.tqnam.filemanager.model.operation;

import com.tqnam.filemanager.model.ItemExplorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Observable;

public abstract class Operation<T> {

    private T mData;

    public Operation(T data) {
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

    public abstract static class TraverseFileOperation<T extends ItemExplorer> extends Operation<List<T>> {

        private ArrayList<Operation> mStreamList;
        private ArrayList<Operation> mExecutingList;
        private ArrayList<Operation> mExecutedList;

        public TraverseFileOperation(List<T> data) {
            super(data);

            mStreamList = new ArrayList<>(10);
            mExecutingList = new ArrayList<>(10);
            mExecutedList = new ArrayList<>(10);
        }

        public ArrayList<Operation> getAllStream() {
            return mStreamList;
        }

        public ArrayList<Operation> getExecutingList() {
            return mExecutingList;
        }

        public ArrayList<Operation> getExecutedList() {
            return mExecutedList;
        }

        public final void traverse() {
            List<T> data = new ArrayList<>(getData());

            while (!data.isEmpty()) {
                T file = data.remove(0);

                Operation operation = createStreamFromData(file);
                mStreamList.add(operation);

                if (file.isDirectory()) {
                    data.addAll((Collection<? extends T>) file.getChild());
                }
            }
        }

        protected void moveToExecuting(Operation operation) {
            if (!mExecutingList.contains(operation))
                mExecutingList.add(operation);
        }

        protected void moveToExecuted(Operation operation) {
            mExecutingList.remove(operation);
            mExecutedList.add(operation);
        }

        public abstract Operation createStreamFromData(T data);
    }

    public abstract static class SingleFileOperation<T extends ItemExplorer> extends Operation<T> {

        public SingleFileOperation(T data) {
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

        public int getProgress() {
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
