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

package com.quangnam.base.utils;

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
