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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_SAVED_HOST, mHost.hashCode());
    }

    @Override
    public void onDestroy() {
        mSubscription.unsubscribe();
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