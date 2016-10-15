package com.quangnam.baseframework;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by quangnam on 1/31/16.
 * Interface to active with {@link (BaseActivity)}
 */
public interface BaseFragmentInterface {

    Context getAppContext();

    /**
     * Safety get activity of Fragment to stop something stupid from {@link Fragment#getActivity()}
     *
     * @return Activity of Fragment
     */
    FragmentActivity getActivitySafe();

    /**
     * Use this function to deal with object animator in support fragment.
     * Can add any animation to start in this anim
     * Time of all animator must be smaller than {@link #getTimeAnimate()}
     */
    void onCreateAnimator(int transit, boolean enter, int nextAnim);

    /**
     * Use this function to define max time to animate, setup with default animation.
     */
    long getTimeAnimate();

    void requestFocusFragment(BaseActivity activity);

    void popupFocusFragment(BaseActivity activity);

    void removeFocusRequest(BaseActivity activity);

    int getPriorityFocusIndex(BaseActivity activity);

    void requestAtPriority(BaseActivity activity, int priority);
}
