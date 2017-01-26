package com.tqnam.filemanager.model.operation;

import com.tqnam.filemanager.model.ItemExplorer;

import java.util.Iterator;
import java.util.List;

import rx.Observable;

/**
 * Created by quangnam on 11/25/16.
 * Project FileManager-master
 * Basic operation include copy, paste, move
 */
public abstract class BasicOperation<T extends ItemExplorer> extends Operation.TraverseFileOperation<T>
        implements Operation.IPause, Operation.ICancel, Operation.IRevert
{
    public static final String TAG = BasicOperation.class.getName();
    public static final int UPDATE_TIME = 800;
    private final Object mLocker;
    protected Observable<? extends BasicUpdatableData> mCurObservable;
    protected BasicUpdatableData mResult;
    private boolean mIsOverwrite;
    private Validator mValidator;
    private long mLastEmitSize;
    private boolean mCancelled;

    public BasicOperation(List<T> data) {
        super(data);

        mIsOverwrite = false;
        mValidator = new Validator();
        mLocker = new Object();
    }

    public long getLastEmitSize() {
        return mLastEmitSize;
    }

    public void setLastEmitSize(long lastEmitSize) {
        mLastEmitSize = lastEmitSize;
    }

    public boolean isOverwrite() {
        return mIsOverwrite;
    }

    public void setOverwrite(boolean overwrite) {
        mIsOverwrite = overwrite;
    }

    public Validator getValidator() {
        return mValidator;
    }

    public String formatTag() {
        return TAG + mResult.hashCode();
    }

    public boolean isRunning() {
        return mResult.getStateValue(OperationState.STATE_RUNNING);
    }

    public void setRunning(boolean running) {
        mResult.setState(OperationState.STATE_RUNNING, running);
        mResult.setState(OperationState.STATE_PAUSE, !running);

        if (running) {
            synchronized (mLocker) {
                mLocker.notifyAll();
            }
        }
    }

    @Override
    public boolean isCancelable() {
        return super.isCancelable()
                && !mResult.getStateValue(OperationState.STATE_FINISHED);
    }

    public boolean isCancelled() {
        return mCancelled;
    }

    public void setCancelled(boolean cancelled) {
        mCancelled = cancelled;
    }

    @Override
    public void cancel() {
        setCancelled(true);
        setRunning(false);        // Resume all operation if operation paused to force cancel
        mResult.setState(OperationState.STATE_CANCELLED, true);
    }

    @Override
    public void revert() {
        mResult.setState(OperationState.STATE_UNDO, true);
    }

    public Object getLocker() {
        return mLocker;
    }

    public void validate() {
    }

    public void setItemValidated(ItemExplorer item) {
        mValidator.setItemSafe(item);
    }

    public void setItemSkipped(ItemExplorer item) {
        getData().remove(item);
    }

    public ItemExplorer getValidatingItem() {
        Iterator<ItemExplorer> iterator = mValidator.getListViolated().iterator();

        return iterator.hasNext() ? iterator.next() : null;
    }

    @Override
    public UpdatableData getUpdateData() {
        return mResult;
    }

    public abstract Observable<? extends BasicUpdatableData> createExecuter();

    @Override
    public final Observable<? extends BasicUpdatableData> execute(Object... arg) {
        if (mCurObservable == null) {
            mCurObservable = createExecuter();
        }

        return mCurObservable;
    }

    public static class BasicUpdatableData extends UpdatableData {

        private float speed;
        private long sizeExecuted;
        private long sizeTotal;

        public float getSpeed() {
            return speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public long getSizeExecuted() {
            return sizeExecuted;
        }

        public void setSizeExecuted(long sizeExecuted) {
            this.sizeExecuted = sizeExecuted;
        }

        public long getSizeTotal() {
            return sizeTotal;
        }

        public void setSizeTotal(long sizeTotal) {
            this.sizeTotal = sizeTotal;
        }

        @Override
        public void validate() {
            setProgress((int)(sizeTotal == 0 ? 100 : (sizeExecuted * 100 + 1) / sizeTotal));
        }

        @Override
        public String toString() {
            return super.toString() +
                    "{speed=" + speed + ", sizeExecuted=" + sizeExecuted + ", sizeTotal=" + sizeTotal + "}";
        }
    }
}