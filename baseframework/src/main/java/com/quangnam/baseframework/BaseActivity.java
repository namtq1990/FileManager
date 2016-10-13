package com.quangnam.baseframework;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
        int priority = mRequestActiveList.indexOf(fragment);
        if (priority == -1) {
            mRequestActiveList.push(fragment);
        } else {
            removeFocusRequest(fragment);
            mRequestActiveList.push(fragment);
        }
    }

    protected void popupFocusFragment() {
        mRequestActiveList.pop();
    }

    public void removeFocusRequest(BaseFragmentInterface fragment) {
        mRequestActiveList.remove(fragment);
    }

    public int getPriorityFocusIndex(BaseFragmentInterface fragment) {
        return mRequestActiveList.indexOf(fragment);
    }

    public void requestAtPriority(int priority, BaseFragmentInterface fragment) {
        mRequestActiveList.add(priority, fragment);
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
}
