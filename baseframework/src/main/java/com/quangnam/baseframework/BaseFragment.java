package com.quangnam.baseframework;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by quangnam on 11/12/15.
 * <p/>
 * Base Class Fragment to use in this app
 * Add it to handle lifecycle and can quickly modify for all fragment
 */
public class BaseFragment extends android.support.v4.app.Fragment implements BaseFragmentInterface {

    private Context mAppContext;
    private String mOldFocusTag;
    private boolean mRestoreFocus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mOldFocusTag = savedInstanceState.getString(ARG_OLD_FOCUS_TAG);
            mRestoreFocus = savedInstanceState.getBoolean(ARG_IS_RESTORE_FOCUS);
        } else {
            mRestoreFocus = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_OLD_FOCUS_TAG, mOldFocusTag);
        outState.putBoolean(ARG_IS_RESTORE_FOCUS, mRestoreFocus);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        BaseActivity activity = (BaseActivity) getActivity();
        if (isRestoreFocus() && !activity.isChangingConfigurations()) {
            if (activity.getFocusFragment() == this && mOldFocusTag != null) {
                Fragment oldFocus = getFragmentManager().findFragmentByTag(mOldFocusTag);

                if (oldFocus instanceof BaseFragmentInterface)
                    ((BaseFragmentInterface) oldFocus).requestFocus();
            }
        }
    }

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

    @Override
    public void requestFocus() {
        BaseActivity activity = (BaseActivity) getActivity();

        if (activity.getFocusFragment() != this) {
            if (activity.getFocusFragment() != null && isRestoreFocus()) {
                mOldFocusTag = ((Fragment) activity.getFocusFragment()).getTag();
            }

            activity.setFocusFragment(this);
        }
    }

    public void popBackFocus() {
        if (isRestoreFocus()) {
            BaseFragmentInterface fragment = (BaseFragmentInterface) getFragmentManager()
                    .findFragmentByTag(mOldFocusTag);
            if (fragment != null) {
                fragment.requestFocus();
            }
        }
    }

    @Override
    public void clearFocus() {

    }

    @Override
    public boolean isRestoreFocus() {
        return mRestoreFocus;
    }

    @Override
    public void setRestoreFocus(boolean isRestoreFocus) {
        mRestoreFocus = isRestoreFocus;
    }

    public Context getAppContext() {
        return mAppContext;
    }
}
