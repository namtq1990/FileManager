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

package com.quangnam.baseframework;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Animation;

import rx.Subscription;

/**
 * Created by quangnam on 11/12/15.
 * <p/>
 * Base Class Fragment to use in this app
 * Add it to handle lifecycle and can quickly modify for all fragment
 */
public class BaseFragment extends android.support.v4.app.Fragment implements BaseFragmentInterface {

    private FragmentImpl mDelegate;

    public BaseFragment() {
        mDelegate = new FragmentImpl(this);
    }

    @Override
    public FragmentActivity getActivitySafe() {
        return mDelegate.getActivitySafe();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDelegate.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mDelegate.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDelegate.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDelegate.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mDelegate.onResume();
    }

    public void requestFocusFragment() {
        mDelegate.requestFocusFragment();
    }

    public void popupFocusFragment() {
        mDelegate.popupFocusFragment();
    }

    public void removeFocusRequest() {
        mDelegate.removeFocusRequest();
    }

    public int getPriorityFocusIndex() {
        return mDelegate.getPriorityFocusIndex();
    }

    public void requestAtPriority(int priority) {
        mDelegate.requestAtPriority(priority);
    }

    @Override
    public void subscribe(Subscription subscription) {
        mDelegate.subscribe(subscription);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return mDelegate.onCreateAnimation(
                super.onCreateAnimation(transit, enter, nextAnim),
                transit,
                enter,
                nextAnim);
    }

    public void onCreateAnimator(int transit, boolean enter, int nextAnim) {
    }

    public long getTimeAnimate() {
        return 0;
    }

    public Context getAppContext() {
        return mDelegate.getAppContext();
    }

    @Override
    public int getSavedHashcode() {
        return mDelegate.getSavedHashcode();
    }
}
