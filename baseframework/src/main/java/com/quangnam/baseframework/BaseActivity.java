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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Stack;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by quangnam on 11/12/15.
 *
 * Base class Activity use for this application.
 * Use this base class so you can handle life cycle to debug or add functional
 */
public class BaseActivity extends AppCompatActivity implements AutoUnsubscribe {

    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    private Stack<BaseFragmentInterface> mRequestActiveList;
    private ArrayList<OnFocusFragmentChanged> mFocusChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestActiveList = new Stack<>();

        if (Config.DEBUG) {
            Log.d("Activity " + toString() + " onCreate");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();

        if (Config.DEBUG) {
            Log.d("Activity " + toString() + " onDestroy");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Config.DEBUG) {
            Log.d("Activity " + toString() + " onPause");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Config.DEBUG) {
            Log.d("Activity " + toString() + " onResume");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (Config.DEBUG) {
            Log.d("Activity " + toString() + " onSaveInstanceState");
        }
    }

    public BaseFragmentInterface getFocusFragment() {
        return mRequestActiveList.isEmpty() ? null : mRequestActiveList.peek();
    }

//    protected void setFocusFragment(BaseFragmentInterface fragment) {
//        if (mFocusFragment != null) {
//            mFocusFragment.clearFocus();
//        }
//
//        mFocusFragment = fragment;
//    }

    void requestFocusFragment(BaseFragmentInterface fragment) {
        BaseFragmentInterface oldFragment = getFocusFragment();

        int priority = mRequestActiveList.indexOf(fragment);
        if (priority == -1) {
            mRequestActiveList.push(fragment);
        } else {
            removeFocusRequest(fragment);
            mRequestActiveList.push(fragment);
        }

        if (oldFragment != fragment) {
            onFocusFragmentChange(oldFragment, fragment);
        }
    }

    void popupFocusFragment() {
        BaseFragmentInterface curFocus = mRequestActiveList.pop();
        onFocusFragmentChange(curFocus, getFocusFragment());
    }

    void removeFocusRequest(BaseFragmentInterface fragment) {
        BaseFragmentInterface curFocusFragment = getFocusFragment();
        mRequestActiveList.remove(fragment);

        if (curFocusFragment == fragment) {
            onFocusFragmentChange(curFocusFragment, getFocusFragment());
        }
    }

    int getPriorityFocusIndex(BaseFragmentInterface fragment) {
        return mRequestActiveList.indexOf(fragment);
    }

    void requestAtPriority(int priority, BaseFragmentInterface fragment) {
        BaseFragmentInterface curFocus = getFocusFragment();
        mRequestActiveList.add(priority, fragment);

        if (fragment == getFocusFragment()) {
            onFocusFragmentChange(curFocus, fragment);
        }
    }

    public void addFocusListener(OnFocusFragmentChanged listener) {
        if (listener == null) return;
        if (mFocusChangeListener == null)
            mFocusChangeListener = new ArrayList<>();

        mFocusChangeListener.add(listener);
    }

    public void removeFocusListener(OnFocusFragmentChanged listener) {
        if (mFocusChangeListener != null) {
            mFocusChangeListener.remove(listener);
        }
    }

    private void onFocusFragmentChange(BaseFragmentInterface oldFragment, BaseFragmentInterface newFragment) {
        if (mFocusChangeListener != null) {
            for (OnFocusFragmentChanged listener : mFocusChangeListener) {
                listener.onFocusFragmentChange(oldFragment, newFragment);
            }
        }
    }

    @Override
    public void subscribe(Subscription subscription) {
        mSubscriptions.add(subscription);
    }

    @Override
    public void onBackPressed() {
        BaseFragmentInterface fragment = getFocusFragment();

        if (fragment instanceof OnBackPressedListener) {
            ((OnBackPressedListener) fragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }


    public BaseDataFragment getDataFragment() {
        return null;
    }

    public interface OnBackPressedListener {
        /**
         * Function handler onBack press in fragment
         * @return true if fragment handled this event
         */
        boolean onBackPressed();
    }

    public interface OnFocusFragmentChanged {
        /**
         * Listener when focus fragment changed
         * @param oldFragment old focus fragment, nullable
         * @param newFragment new focus fragment, nullable
         */
        void onFocusFragmentChange(BaseFragmentInterface oldFragment, BaseFragmentInterface newFragment);
    }
}
