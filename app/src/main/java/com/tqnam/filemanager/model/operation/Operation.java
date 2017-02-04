package com.tqnam.filemanager.model.operation;

import com.quangnam.baseframework.Log;
import com.tqnam.filemanager.model.ItemExplorer;
import com.tqnam.filemanager.model.operation.propertyView.OperationPropertyView;
import com.tqnam.filemanager.utils.OperationManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Observable;

public abstract class Operation<T> {

    protected OperationPropertyView mPropertyView;

    private T mData;

    public Operation(T data) {
        mData = data;
    }

    public abstract Observable<?> execute(Object... arg);

    public boolean isCancelable() {
        return this instanceof ICancel;
    }

    public boolean isUndoable() {
        return this instanceof IRevert;
    }

    public boolean isRestartable() {
        return this instanceof IRestart;
    }

    public boolean isAbleToPause() {
        return this instanceof IPause;
    }

    public boolean isUpdatable() {
        return getUpdateData() != null;
    }

    public int getCategory() {
        return OperationManager.CATEGORY_OTHER;
    }

    public abstract String getSourcePath();

    public abstract String getDestinationPath();

    public OperationPropertyView getPropertyView() {
        return mPropertyView;
    }

    public T getData() {
        return mData;
    }

    public abstract UpdatableData getUpdateData();

    public interface IPause {
        boolean isRunning();

        void setRunning(boolean isRunning);
    }

    public interface ICancel {
        void cancel();
    }

    public interface IRevert {
        void revert();
    }

    public interface IRestart {
        void restart();
    }

    public interface OnStateChangeListener {
        void onStateChanging(int mode, boolean newValue);
        void onStateChanged(int mode, boolean oldValue, boolean newValue);
    }

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
        private int state;

        private ArrayList<OnStateChangeListener> mOnStateChangeListeners;

        public UpdatableData() {
            mOnStateChangeListeners = new ArrayList<>();
            setState(OperationState.STATE_RUNNING, true);
        }

        public final boolean isFinished() {
            return getStateValue(OperationState.STATE_FINISHED);
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
            if (this.progress >= 100) {
                setState(OperationState.STATE_FINISHED, true);
            }
        }

        public long getOperatorHashcode() {
            return operatorHashcode;
        }

        public void setOperatorHashcode(int hashcode) {
            operatorHashcode = hashcode;
        }

        public boolean isError() {
            return getStateValue(OperationState.STATE_ERROR);
        }

        public void setError(boolean error) {
            setState(OperationState.STATE_PAUSE, false);
            setState(OperationState.STATE_RUNNING, false);
            setState(OperationState.STATE_ERROR, error);
        }

        public void setState(int stateMode, boolean value) {
            Log.d("Setting state " + stateMode + " value " + value + " in cur state: " + getState());
            boolean stateChange = false;
            if (getStateValue(stateMode) != value) {
                // State changing, so notify all
                stateChange = true;
                for (OnStateChangeListener listener : mOnStateChangeListeners) {
                    listener.onStateChanging(stateMode, value);
                }
            }

            if (value) {
                state |= stateMode;
            } else {
                state &= ~stateMode;
            }
            state &= (value ? stateMode : ~stateMode);

            if (stateChange) {
                for (OnStateChangeListener listener : mOnStateChangeListeners) {
                    listener.onStateChanged(stateMode, !value, value);
                }
            }
        }

        public boolean getStateValue(int stateMode) {
            return (state & stateMode) != 0;
        }

        public int getState() {
            return state;
        }

        public void validate() {
        }

        public void registerStateChangeListener(OnStateChangeListener listener) {
            if (!mOnStateChangeListeners.contains(listener))
                mOnStateChangeListeners.add(listener);
        }

        public void unregisterStateChangeListener(OnStateChangeListener listener) {
            mOnStateChangeListeners.remove(listener);
        }

        @Override
        public String toString() {
            return super.toString() + "{"
                    + "state=" + getState() + ","
                    + "progress=" + progress + "}";
        }

    }

    public static class OperationState {
        public static final int STATE_FINISHED = 0x1;
        public static final int STATE_ERROR = 0x2;
        public static final int STATE_CANCELLED = 0x4;
        public static final int STATE_RUNNING = 0x8;
        public static final int STATE_PAUSE = 0x10;
        public static final int STATE_UNDO = 0x20;
    }

}
