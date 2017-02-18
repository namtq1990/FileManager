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

package com.tqnam.filemanager.model.operation;

import com.quangnam.baseframework.utils.RxCacheWithoutError;
import com.tqnam.filemanager.model.ItemExplorer;

import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

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
    protected boolean mEmitResult;
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
        mEmitResult = true;

        mResult = new BasicUpdatableData();
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

    public void performLockIfOperationPaused() {
        synchronized (mLocker) {
            if (!isRunning()) {
                try {
                    mLocker.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            mCurObservable = createExecuter()
                    .filter(new Func1<BasicUpdatableData, Boolean>() {
                        @Override
                        public Boolean call(BasicUpdatableData basicUpdatableData) {
                            boolean emitResult = mEmitResult;
                            mEmitResult = mResult.getStateValue(OperationState.STATE_RUNNING);
                            return emitResult || mEmitResult;
                        }
                    })
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (mResult != null) {
                                mResult.setError(true);
                            }
                        }
                    })
                    .compose(new RxCacheWithoutError<BasicUpdatableData>(1));
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
