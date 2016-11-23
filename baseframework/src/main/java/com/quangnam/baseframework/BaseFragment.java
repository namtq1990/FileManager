package com.quangnam.baseframework;

import android.content.Context;
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
    public void onDestroy() {
        super.onDestroy();
        mDelegate.onDestroy();
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
}
