package com.quangnam.baseframework;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat implements BaseFragmentInterface {

    private Context mAppContext;

    @Override
    public FragmentActivity getActivitySafe() {
        BaseApplication application = (BaseApplication) getAppContext();
        return getActivity() != null ? getActivity() : (FragmentActivity) application.getCurActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAppContext = context.getApplicationContext();
    }

    public void requestFocusFragment(BaseActivity activity) {
        activity.requestFocusFragment(this);
    }

    public void popupFocusFragment(BaseActivity activity) {
        activity.popupFocusFragment();
    }

    public void removeFocusRequest(BaseActivity activity) {
        activity.removeFocusRequest(this);
    }

    public int getPriorityFocusIndex(BaseActivity activity) {
        return activity.getPriorityFocusIndex(this);
    }

    public void requestAtPriority(BaseActivity activity, int priority) {
        activity.requestAtPriority(priority, this);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation anim = super.onCreateAnimation(transit, enter, nextAnim);

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

    public void onCreateAnimator(int transit, boolean enter, int nextAnim) {
    }

    public long getTimeAnimate() {
        return 0;
    }

    public Context getAppContext() {
        return mAppContext;
    }
}
