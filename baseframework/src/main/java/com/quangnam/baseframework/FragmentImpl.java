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
import android.support.v4.app.FragmentActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by quangnam on 11/22/16.
 * Project FileManager-master
 */
class FragmentImpl implements BaseFragmentInterface {
    private static final String TAG = FragmentImpl.class.getName();
    private static final String ARG_SAVED_HOST = TAG + "_savedHostID";

    int mSavedHostID;
    BaseApplication mAppContext;
    BaseActivity mActivity;
    CompositeSubscription mSubscription;
    BaseFragmentInterface mHost;

    FragmentImpl(BaseFragmentInterface host) {
        mSubscription = new CompositeSubscription();
        mHost = host;
        mSavedHostID = -1;
    }

    @Override
    public void onAttach(Context context) {
        mAppContext = (BaseApplication) context.getApplicationContext();
        mActivity = (BaseActivity) context;
    }

    @Override
    public void onCreate(Bundle savedState) {
        if (savedState != null) {
            mSavedHostID = savedState.getInt(ARG_SAVED_HOST);
        }

        if (Config.DEBUG) {
            Log.d("Fragment " + mHost + " onCreate");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_SAVED_HOST, mHost.hashCode());

        if (Config.DEBUG) {
            Log.d("Fragment " + mHost + " onSaveInstanceState");
        }
    }

    @Override
    public void onDestroy() {
        mSubscription.unsubscribe();

        if (Config.DEBUG) {
            Log.d("Fragment " + mHost + " onDestroy");
        }
    }

    @Override
    public void onResume() {
        if (Config.DEBUG) {
            Log.d("Fragment " + mHost + " onResume");
        }
    }

    @Override
    public void onPause() {
        if (Config.DEBUG) {
            Log.d("Fragment " + mHost + " onPause");
        }
    }

    @Override
    public Context getAppContext() {
        return mAppContext;
    }

    @Override
    public int getSavedHashcode() {
        return mSavedHostID;
    }

    @Override
    public FragmentActivity getActivitySafe() {

        return getActivity() != null
                ? getActivity()
                : mActivity;
    }

    public Animation onCreateAnimation(Animation hostAnimation, int transit, boolean enter, int nextAnim) {
        Animation anim = hostAnimation;

        long timeAnim = getTimeAnimate();
        if (timeAnim != 0) {

            if (anim == null) {
                anim = AnimationUtils.loadAnimation(getActivitySafe(), R.anim.anim_base_time);
            }
            anim.setDuration(timeAnim);
            onCreateAnimator(transit, enter, nextAnim);
        }

        return anim;
    }

    BaseActivity getActivity() {
        return mActivity;
    }

    @Override
    public void onCreateAnimator(int transit, boolean enter, int nextAnim) {
        mHost.onCreateAnimator(transit, enter, nextAnim);
    }

    @Override
    public long getTimeAnimate() {
        return mHost.getTimeAnimate();
    }

    @Override
    public void requestFocusFragment() {
        mActivity.requestFocusFragment(mHost);
    }

    @Override
    public void popupFocusFragment() {
        mActivity.popupFocusFragment();
    }

    @Override
    public void removeFocusRequest() {
        mActivity.removeFocusRequest(mHost);
    }

    @Override
    public int getPriorityFocusIndex() {
        return mActivity.getPriorityFocusIndex(mHost);
    }

    @Override
    public void requestAtPriority(int priority) {
        mActivity.requestAtPriority(priority, mHost);
    }

    @Override
    public void subscribe(Subscription subscription) {
        mSubscription.add(subscription);
    }
}
