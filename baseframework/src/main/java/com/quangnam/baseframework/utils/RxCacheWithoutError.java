package com.quangnam.baseframework.utils;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func0;

/**
 * Created by quangnam on 11/20/16.
 * Project FileManager-master
 */
public class RxCacheWithoutError<T> implements Observable.Transformer<T, T> {

    //    private final Semaphore singlePermit = new Semaphore(1);

    private Observable<T> cache = null;
    private Observable<T> inProgress = null;
    private boolean isForceReplay;

    private int mBufferSize;

    public RxCacheWithoutError(int bufferSize) {
        mBufferSize = bufferSize;
        setForceReplay(false);
    }

    private Observable<T> createWhenObserverSubscribes(Observable<T> source)
    {
//        singlePermit.acquireUninterruptibly();

        Observable<T> cached = cache;
        if (cached != null && !isForceReplay) {
//            singlePermit.release();
            return cached;
        }

        if (inProgress == null) {
            inProgress = source
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            onSuccess();
                        }
                    })
                    .doOnTerminate(new Action0() {
                        @Override
                        public void call() {
                            onTermination();
                        }
                    });
            inProgress = mBufferSize == 0 ? inProgress.cache() : inProgress.cache(mBufferSize);
        }

        return inProgress;
    }

    public void setForceReplay(boolean forceReplay) {
        isForceReplay = forceReplay;
    }

    private void onSuccess() {
        cache = inProgress;
    }

    private void onTermination() {
        inProgress = null;
//        singlePermit.release();
    }

    @Override
    public Observable<T> call(final Observable<T> source) {
        return Observable.defer(new Func0<Observable<T>>() {
            @Override
            public Observable<T> call() {
                return createWhenObserverSubscribes(source);
            }
        });
    }
}
