package com.tqnam.filemanager;

import android.support.v4.app.FragmentActivity;

/**
 * Created by quangnam on 11/12/15.
 *
 * Base Class Fragment to use in this app
 * Add it to handle lifecycle and can quickly modify for all fragment
 *
 */
public class BaseFragment extends android.support.v4.app.Fragment {


    /**
     * Safety get activity of Fragment to stop something stupid from {@link #getActivity()}
     * @return Activity of Fragment
     */
    public FragmentActivity getActivitySafe() {
        return getActivity() != null ? getActivity() : (FragmentActivity) Application.getInstance().getGlobalData().getCurActivity();
    }
}
