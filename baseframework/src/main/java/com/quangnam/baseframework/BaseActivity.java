package com.quangnam.baseframework;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Stack;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by quangnam on 11/12/15.
 *
 * Base class Activity use for this application.
 * Use this base class so you can handle life cycle to debug or add functional
 */
public class BaseActivity extends AppCompatActivity {

    private CompositeSubscription mLocalSubs = new CompositeSubscription();
    private Stack<BaseFragmentInterface> mRequestActiveList;
    private ArrayList<OnFocusFragmentChanged> mFocusChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestActiveList = new Stack<>();
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

    public void requestFocusFragment(BaseFragmentInterface fragment) {
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

    protected void popupFocusFragment() {
        BaseFragmentInterface curFocus = mRequestActiveList.pop();
        onFocusFragmentChange(curFocus, getFocusFragment());
    }

    public void removeFocusRequest(BaseFragmentInterface fragment) {
        BaseFragmentInterface curFocusFragment = getFocusFragment();
        mRequestActiveList.remove(fragment);

        if (curFocusFragment == fragment) {
            onFocusFragmentChange(curFocusFragment, getFocusFragment());
        }
    }

    public int getPriorityFocusIndex(BaseFragmentInterface fragment) {
        return mRequestActiveList.indexOf(fragment);
    }

    public void requestAtPriority(int priority, BaseFragmentInterface fragment) {
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
    protected void onDestroy() {
        super.onDestroy();
        getLocalSubscription().unsubscribe();
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

    public CompositeSubscription getLocalSubscription() {
        return mLocalSubs;
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
        void onFocusFragmentChange(BaseFragmentInterface oldFragment, BaseFragmentInterface newFragment);
    }
}
