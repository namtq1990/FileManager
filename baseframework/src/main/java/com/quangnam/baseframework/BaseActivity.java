package com.quangnam.baseframework;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by quangnam on 11/12/15.
 *
 * Base class Activity use for this application.
 * Use this base class so you can handle life cycle to debug or add functional
 */
public class BaseActivity extends AppCompatActivity {

    private BaseFragmentInterface mFocusFragment;

    public BaseFragmentInterface getFocusFragment() {
        return mFocusFragment;
    }

    void setFocusFragment(BaseFragmentInterface fragment) {
        if (mFocusFragment != null) {
            mFocusFragment.clearFocus();
        }

        mFocusFragment = fragment;
    }

}
