package com.quangnam.baseframework;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.animation.Animation;

import rx.Subscription;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat implements BaseFragmentInterface {

    private FragmentImpl mDelegate;

    public BasePreferenceFragment() {
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
    public void onCreate(Bundle savedInstanceState) {
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
    public int getSavedHashcode() {
        return mDelegate.getSavedHashcode();
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
}
