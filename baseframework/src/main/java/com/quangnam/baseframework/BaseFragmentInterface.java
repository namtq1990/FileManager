package com.quangnam.baseframework;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by quangnam on 1/31/16.
 * Interface to active with {@link (BaseActivity)}
 */
public interface BaseFragmentInterface extends AutoUnsubscribe {

    void onAttach(Context context);
    void onCreate(Bundle savedState);
    void onSaveInstanceState(Bundle outState);
    void onDestroy();

    Context getAppContext();

    int getSavedHashcode();

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

    void requestFocusFragment();

    void popupFocusFragment();

    void removeFocusRequest();

    int getPriorityFocusIndex();

    void requestAtPriority(int priority);

}
