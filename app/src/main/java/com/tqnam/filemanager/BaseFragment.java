package com.tqnam.filemanager;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

/**
 * Created by quangnam on 11/12/15.
 *
 * Base Class Fragment to use in this app
 * Add it to handle lifecycle and can quickly modify for all fragment
 *
 */
public class BaseFragment extends android.support.v4.app.Fragment {

    private Context mAppContext;

    /**
     * Safety get activity of Fragment to stop something stupid from {@link #getActivity()}
     * @return Activity of Fragment
     */
    public FragmentActivity getActivitySafe() {
        return getActivity() != null ? getActivity() : (FragmentActivity) Application.getInstance().getGlobalData().getCurActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAppContext = context.getApplicationContext();
    }

    public Context getAppContext() {
        return  mAppContext;
    }
}
